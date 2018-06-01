package org.binas.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import javax.xml.ws.Response;

import org.binas.domain.exception.BadInitException;
import org.binas.domain.exception.InsufficientCreditsException;
import org.binas.domain.exception.InvalidEmailException;
import org.binas.domain.exception.StationNotFoundException;
import org.binas.domain.exception.UserAlreadyExistsException;
import org.binas.domain.exception.UserAlreadyHasBinaException;
import org.binas.domain.exception.UserHasNoBinaException;
import org.binas.domain.exception.UserNotFoundException;
import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.GetBalanceResponse;
import org.binas.station.ws.NoBinaAvail_Exception;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.SetBalanceResponse;
import org.binas.station.ws.UserNotFound_Exception;
import org.binas.station.ws.cli.StationClient;
import org.binas.station.ws.cli.StationClientException;
import org.binas.ws.StationView;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

/**
 * BinasManager class 
 * 
 * Class that have the methods used to get/Return Bina, beginning a station, querying all stations, etc.
 *
 */
public class BinasManager {
	/**
	 * UDDI server URL
	 */
	private String uddiURL = null;

	/**
	 * Station name
	 */
	private String stationTemplateName = null;

	// Singleton -------------------------------------------------------------

	private BinasManager() {
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder {
		private static final BinasManager INSTANCE = new BinasManager();
	}

	public static synchronized BinasManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	// Binas Logic ----------------------------------------------------------

	public User createUser(String email) throws UserAlreadyExistsException, InvalidEmailException {
		return UsersManager.getInstance().RegisterNewUser(email);
	}

	public User getUser(String email) throws UserNotFoundException {
		return UsersManager.getInstance().getUser(email);
	}
	
	public void rentBina(String stationId, String email) throws UserNotFoundException, InsufficientCreditsException, UserAlreadyHasBinaException, StationNotFoundException, NoBinaAvail_Exception {
		User user = getUser(email);
		
		synchronized (user) {
		
			//validate user can rent
			user.validateCanRentBina();
			
			//validate station can rent
			StationClient stationCli = getStation(stationId);
			
			//try {
				//Ve o balance
				int 
				balance = BinasManager.getInstance().auxGetBalance(email);
				if(balance <= 0) {
					throw new InsufficientCreditsException("Nao tem credito suficiente: " + email);
				}
				
				//O client aluga a bicileta
				stationCli.getBina();
				
				auxSetBalance(email, balance -1, maxTag(email)+1);
			//} 
			/*catch (UserNotFound_Exception e) {
				throw new UserNotFoundException("User not found: " + email);
			}*/
			//apply rent action to user
			user.effectiveRent();
		}
	}
	
	public void returnBina(String stationId, String email) throws UserNotFoundException, NoSlotAvail_Exception, UserHasNoBinaException, StationNotFoundException {
		User user = getUser(email);
		synchronized (user) {
			//validate user can rent
			user.validateCanReturnBina();
			
			//validate station can rent
			StationClient stationCli = getStation(stationId);
			
			int prize = stationCli.returnBina();
			int balance;
			//try {
				balance = BinasManager.getInstance().auxGetBalance(email);
				
				auxSetBalance(email, balance + prize, maxTag(email)+1);
			/*} catch (UserNotFound_Exception e) {
				throw new UserNotFoundException("User not found: " + email);
			}*/

			
			//apply rent action to user
			user.effectiveReturn(prize);
		}			
	}

	public StationClient getStation(String stationId) throws StationNotFoundException {

		Collection<String> stations = this.getStations();
		String uddiUrl = BinasManager.getInstance().getUddiURL();
		
		for (String s : stations) {
			try {
				StationClient sc = new StationClient(uddiUrl, s);
				org.binas.station.ws.StationView sv = sc.getInfo();
				String idToCompare = sv.getId();
				if (idToCompare.equals(stationId)) {
					return sc;
				}
			} catch (StationClientException e) {
				continue;
			}
		}
		
		throw new StationNotFoundException();
	}
	
	public void balanceError(int balance) {
		if(balance == -1) {
			System.err.println("Error while polling in getBalance :(");
		}
	}
	
	//funcao que chama o getBalance com polling, devolve -1 em caso de erro e o balance, com maior tag, 
	//em caso de sucesso
	public int auxGetBalance(String email) {
		StationClient st1 = null;
		StationClient st2 = null;
		StationClient st3 = null;
		int tag1 = 0;
		int tag2  = 0;
		
		ArrayList<Response<GetBalanceResponse>> respostas = new ArrayList<Response<GetBalanceResponse>>();
		
		//colecao com todas as stations
		Collection<String> stations = BinasManager.getInstance().getStations();
		//ArrayList<Stations> = 
		//System.out.println(stations);
		try {
			if(stations.size() == 3) {
				st1 = BinasManager.getInstance().getStation((String)stations.toArray()[0]);
				st2 = BinasManager.getInstance().getStation((String)stations.toArray()[1]);
				st3 = BinasManager.getInstance().getStation((String)stations.toArray()[2]);
			}
			else if(stations.size() == 2) {
				st1 = BinasManager.getInstance().getStation((String)stations.toArray()[0]);
				st2 = BinasManager.getInstance().getStation((String)stations.toArray()[1]);
				st3 = BinasManager.getInstance().getStation((String)stations.toArray()[1]);
			}
			else {
				System.err.println("Something unexpected ocurred :(");
			}
		} catch (StationNotFoundException e1) {
			System.out.println("StationNotFoundException no get da estacao");
		}
	
		Response<GetBalanceResponse> response1 = st1.getBalanceAsync(email);
		Response<GetBalanceResponse> response2 = st2.getBalanceAsync(email);
		Response<GetBalanceResponse> response3 = st3.getBalanceAsync(email);
		
		
		while(!response1.isDone() && !response2.isDone() || 
			  !response1.isDone() && !response3.isDone() ||
			  !response2.isDone() && !response3.isDone()) {
			try {
				//System.out.println("Wainting for a response...");
				Thread.sleep(100 /* milliseconds */);
			} catch (InterruptedException e) {
				System.out.println("InterruptedException while sleeping... zzz...");
				//e.printStackTrace();
			}
		}
		
		//Tag1
		if(response1.isDone()) {
			respostas.add(response1);

		}	
		//Tag2
		if(response2.isDone()) {	
			respostas.add(response2);
		}
		//Tag3
		if(response3.isDone()) {
			respostas.add(response3);
		}
		
		if(respostas.size() >= 2) {
			//System.out.println("Already have 2 responses");
			try {
				tag1 = respostas.get(0).get().getBalanceInfo().getTag();
				tag2 = respostas.get(1).get().getBalanceInfo().getTag();
				if(tag1>=tag2) {
					int balance = respostas.get(0).get().getBalanceInfo().getBalance();
					return balance;
				}
				else {
					int balance = respostas.get(1).get().getBalanceInfo().getBalance();
					return balance;
				}
			} catch (InterruptedException e) {
				System.out.println("InterruptedException while getBalance from the list ");
				//e.printStackTrace();
			} catch (ExecutionException e) {
				/*st1.setBalance(email, UsersManager.getInstance().initialBalance.get());
				st2.setBalance(email, UsersManager.getInstance().initialBalance.get());
				st3.setBalance(email, UsersManager.getInstance().initialBalance.get());*/
				
				//O valor da tag ao inicio e 1
				auxSetBalance(email, UsersManager.getInstance().initialBalance.get(), 1);
				
				return UsersManager.getInstance().initialBalance.get();
			}
		}
		return -1;
	}

	//faz o set de email, com o seu respetivo balance e tag nas 3 estacoes
	public void auxSetBalance(String email, int balance, int tag) {
		/*int tagAtual = maxTag(email);
		if(tag > tagAtual) {*/
			StationClient st1 = null;
			StationClient st2 = null;
			StationClient st3 = null;
			int count = 0 ;
			
			//Chamada normal
			//stationClient.setBalance(email, UsersManager.getInstance().initialBalance.get());
			
			//Chamada assincrona -> nao e preciso esperar por resposta porque é void
			//stationClient.setBalanceAsync(email, UsersManager.getInstance().initialBalance.get());
			//colecao com todas as stations
			Collection<String> stations = BinasManager.getInstance().getStations();
			//ArrayList<Stations> = 
			//System.out.println(stations);
			try {
				if(stations.size() == 3) {
					st1 = BinasManager.getInstance().getStation((String)stations.toArray()[0]);
					st2 = BinasManager.getInstance().getStation((String)stations.toArray()[1]);
					st3 = BinasManager.getInstance().getStation((String)stations.toArray()[2]);
				}
				else if(stations.size() == 2) {
					st1 = BinasManager.getInstance().getStation((String)stations.toArray()[0]);
					st2 = BinasManager.getInstance().getStation((String)stations.toArray()[1]);
					st3 = BinasManager.getInstance().getStation((String)stations.toArray()[1]);
				}
				else {
					System.err.println("Something unexpected ocurred :(");
				}
			} catch (StationNotFoundException e1) {
				System.out.println("StationNotFoundException no get da estacao");
			}
			
			//System.out.println("Stoped");
			Response<SetBalanceResponse> response1 = st1.setBalanceAsync(email, balance, tag);
			Response<SetBalanceResponse> response2 = st2.setBalanceAsync(email, balance, tag);
			Response<SetBalanceResponse> response3 = st3.setBalanceAsync(email, balance, tag);
			
			
			while(!response1.isDone() && !response2.isDone() || 
				  !response1.isDone() && !response3.isDone() ||
				  !response2.isDone() && !response3.isDone()) {
				try {
					//System.out.println("Wainting for a response...");
					Thread.sleep(100 /* milliseconds */);
				} catch (InterruptedException e) {
					System.out.println("InterruptedException while sleeping... zzz...");
					//e.printStackTrace();
				}
			}
			
			//Tag1
			if(response1.isDone()) {
				count++;

			}	
			//Tag2
			if(response2.isDone()) {	
				count++;
			}
			//Tag3
			if(response3.isDone()) {
				count++;
			}
			if (count >= 2) {
				st1.setBalanceAsync(email, balance, tag);
				st2.setBalanceAsync(email, balance, tag);
				st3.setBalanceAsync(email, balance, tag);
			}
			/*st1.setBalanceAsync(email, balance, tag);
			st2.setBalanceAsync(email, balance, tag);
			st3.setBalanceAsync(email, balance, tag);*/
		//}
	}
	
	//Recebe um email e devolve a maior tag associada
	public int maxTag(String email) {
		StationClient st1 = null;
		StationClient st2 = null;
		StationClient st3 = null;
		int tag1 = 0;
		int tag2  = 0;
		
		ArrayList<Response<GetBalanceResponse>> respostas = new ArrayList<Response<GetBalanceResponse>>();
	
		//colecao com todas as stations
		Collection<String> stations = BinasManager.getInstance().getStations();
		//ArrayList<Stations> = 
		//System.out.println(stations);
		try {
			if(stations.size() == 3) {
				st1 = BinasManager.getInstance().getStation((String)stations.toArray()[0]);
				st2 = BinasManager.getInstance().getStation((String)stations.toArray()[1]);
				st3 = BinasManager.getInstance().getStation((String)stations.toArray()[2]);
			}
			else if(stations.size() == 2) {
				st1 = BinasManager.getInstance().getStation((String)stations.toArray()[0]);
				st2 = BinasManager.getInstance().getStation((String)stations.toArray()[1]);
				st3 = BinasManager.getInstance().getStation((String)stations.toArray()[1]);
			}
			else {
				System.err.println("Something unexpected ocurred :(");
			}
		} catch (StationNotFoundException e1) {
			System.out.println("StationNotFoundException no get da estacao");
		}
	
		Response<GetBalanceResponse> response1 = st1.getBalanceAsync(email);
		Response<GetBalanceResponse> response2 = st2.getBalanceAsync(email);
		Response<GetBalanceResponse> response3 = st3.getBalanceAsync(email);
		
		
		while(!response1.isDone() && !response2.isDone() || 
			  !response1.isDone() && !response3.isDone() ||
			  !response2.isDone() && !response3.isDone()) {
			try {
				//System.out.println("Wainting for a response...");
				Thread.sleep(100 /* milliseconds */);
			} catch (InterruptedException e) {
				System.out.println("InterruptedException while sleeping... zzz...");
				//e.printStackTrace();
			}
		}
		
		//Tag1
		if(response1.isDone()) {
			respostas.add(response1);

		}	
		//Tag2
		if(response2.isDone()) {	
			respostas.add(response2);
		}
		//Tag3
		if(response3.isDone()) {
			respostas.add(response3);
		}
		
		if(respostas.size() >= 2) {
			//System.out.println("Already have 2 responses");
			try {
				tag1 = respostas.get(0).get().getBalanceInfo().getTag();
				tag2 = respostas.get(1).get().getBalanceInfo().getTag();
				if(tag1>=tag2) {
					int tag = respostas.get(0).get().getBalanceInfo().getTag();
					return tag;
				}
				else {
					int tag = respostas.get(1).get().getBalanceInfo().getTag();
					return tag;
				}
			} catch (InterruptedException e) {
				System.out.println("InterruptedException while getBalance from the list ");
				//e.printStackTrace();
			} catch (ExecutionException e) {
				//st1.setBalance(email, UsersManager.getInstance().initialBalance.get());
				//st2.setBalance(email, UsersManager.getInstance().initialBalance.get());
				//st3.setBalance(email, UsersManager.getInstance().initialBalance.get());
				
				//O valor da tag ao inicio e 1
				auxSetBalance(email, UsersManager.getInstance().initialBalance.get(), 1);

				
				return 1;
			}
		}
		return -1;
	}
	// UDDI ------------------------------------------------------------------

	public void initUddiURL(String uddiURL) {
		setUddiURL(uddiURL);
	}

	public void initStationTemplateName(String stationTemplateName) {
		setStationTemplateName(stationTemplateName);
	}

	public String getUddiURL() {
		return uddiURL;
	}

	private void setUddiURL(String url) {
		uddiURL = url;
	}

	private void setStationTemplateName(String sn) {
		stationTemplateName = sn;
	}

	public String getStationTemplateName() {
		return stationTemplateName;
	}

	/**
	 * Get list of stations for a given query
	 * 
	 * @return List of stations
	 */
	public Collection<String> getStations() {
		Collection<UDDIRecord> records = null;
		Collection<String> stations = new ArrayList<String>();
		try {
			UDDINaming uddi = new UDDINaming(uddiURL);
			records = uddi.listRecords(stationTemplateName + "%");
			for (UDDIRecord u : records)
				stations.add(u.getOrgName());
		} catch (UDDINamingException e) {
		}
		return stations;
	}

	public void reset() {
		UsersManager.getInstance().reset();
	}

	public void init(int userInitialPoints) throws BadInitException {
		if(userInitialPoints < 0) {
			throw new BadInitException();
		}
		UsersManager.getInstance().init(userInitialPoints);
	}

	/**
	 * 
	 * Inits a Station with a determined ID, coordinates, capacity and returnPrize
	 * 
	 * @param stationId
	 * @param x
	 * @param y
	 * @param capacity
	 * @param returnPrize
	 * @throws BadInitException
	 * @throws StationNotFoundException
	 */
	public void testInitStation(String stationId, int x, int y, int capacity, int returnPrize) throws BadInitException, StationNotFoundException {
		//validate station can rent
		StationClient stationCli;
		try {
			stationCli = getStation(stationId);
			stationCli.testInit(x, y, capacity, returnPrize);
		} catch (BadInit_Exception e) {
			throw new BadInitException(e.getMessage());
		}
		
	}
}
