package org.binas.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

/*
import org.binas.station.domain.Coordinates;
import org.binas.station.domain.Station;
import org.binas.station.domain.exception.BadInitException;
import org.binas.station.domain.exception.NoBinaAvailException;
import org.binas.station.domain.exception.NoSlotAvailException;*/

import org.binas.domain.BinasManager;
import org.binas.domain.OrdenatorDistances;
import org.binas.domain.User;
import org.binas.station.ws.NoSlotAvail_Exception;
//import org.binas.station.ws.StationView;
import org.binas.station.ws.cli.StationClient;
import org.binas.station.ws.cli.StationClientException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

/**
 * This class implements the Web Service port type (interface). The annotations
 * below "map" the Java class to the WSDL definitions.
 */
// TODO


// endpoint interface (nome do tipo Java do PortType)
// wsdlLocation (nome do ficheiro WSDL)   wsdlLocation = "...",
// name (definido no WSDL)
// portName (WSDL) "...Port",
//  targetNamespace (WSDL) "...",
// serviceName (WSDL) "...Service"

@WebService(endpointInterface = "org.binas.ws.BinasPortType",
	wsdlLocation = "binas.1_0.wsdl",
	name ="BinasWebService",
	portName = "BinasPort",
	targetNamespace= "http://ws.binas.org/",
	serviceName = "BinasService")

public class BinasPortImpl implements BinasPortType {
	
	private BinasEndpointManager endpointManager;
	
	public BinasPortImpl(BinasEndpointManager endpoint) {
		this.endpointManager = endpoint;
	}
	
	
	
	@Override
	public List<StationView> listStations(Integer numberOfStations, CoordinatesView coordinates) {
		Collection<UDDIRecord> stations = getStations();
		
		Map <String, Double> distancesStation = new HashMap <String, Double>();
		OrdenatorDistances ordenated = new OrdenatorDistances(distancesStation);	
		TreeMap<String, Double> distancesOrdenated = new TreeMap<String, Double>(ordenated);
		
		List<StationView> binasStationView = new ArrayList<StationView>();
		org.binas.station.ws.StationView stationSView;

		int xGiven = coordinates.getX();
		int yGiven = coordinates.getY();
		
		for (UDDIRecord station : stations) {
			StationClient sClient;
			try {
				sClient = new StationClient ( station.getUrl() , station.getOrgName());
			
				stationSView = sClient.getInfo();
				
				int xstation = stationSView.getCoordinate().getX();
				int ystation = stationSView.getCoordinate().getY();
				int distX = Math.abs(xstation - xGiven);
				int distY = Math.abs(ystation - yGiven);
				Double distancia = Math.hypot(distX, distY);
				distancesStation.put(stationSView.getId(), distancia);
			} catch (StationClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		distancesOrdenated.putAll(distancesStation);
		
		if (numberOfStations > stations.size())
			numberOfStations = stations.size();
		for (int i = 0; i < numberOfStations; i++) {
			/*System.out.print("lol");
			System.out.println(distancesOrdenated.size() );*/
			//String stName = distancesOrdenated.firstKey();
			String stName = distancesOrdenated.pollFirstEntry().getKey();
			
			for(UDDIRecord stationX: stations ) {
				if(stName.equals(stationX.getOrgName())) {
					try {

						CoordinatesView coord = new CoordinatesView();
						StationClient sClient = new StationClient ( stationX.getUrl() , stationX.getOrgName());

						StationView binasStView =  new StationView();
						stationSView = sClient.getInfo();
						
						binasStView.setId(stationSView.getId());
						
						binasStView.setCapacity(stationSView.getCapacity() );
						coord.setX(stationSView.getCoordinate().getX());
						coord.setY(stationSView.getCoordinate().getY());
						binasStView.setCoordinate(coord);
						
						binasStView.setTotalGets( stationSView.getTotalGets());
						binasStView.setTotalReturns(stationSView.getTotalReturns());
						
						binasStView.setAvailableBinas(stationSView.getAvailableBinas() );
						binasStView.setFreeDocks(stationSView.getFreeDocks() );
						

						binasStationView.add(binasStView);
					} catch (StationClientException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		return binasStationView;
	}
	
	@Override
	public StationView getInfoStation(String stationId) throws InvalidStation_Exception {
		/* Get Stations */
		StationView binasStationView =  new StationView();
		Collection<UDDIRecord> stations = getStations();

		/*System.out.print("stationId");
		System.out.println(stationId);*/
		for(UDDIRecord stationX: stations ) {/*
			System.out.print("olauddi");
			System.out.print(stationX.getOrgName());*/

			CoordinatesView coord = new CoordinatesView();
			if(stationId.equals(stationX.getOrgName())) {
				//System.out.print("entrou");
				try {
					StationClient sClient = new StationClient ( stationX.getUrl() , stationX.getOrgName());
					
					org.binas.station.ws.StationView stationSView = sClient.getInfo();
					
					binasStationView.setId(stationSView.getId());
					
					binasStationView.setCapacity(stationSView.getCapacity() );
					coord.setX(stationSView.getCoordinate().getX());
					coord.setY(stationSView.getCoordinate().getY());
					binasStationView.setCoordinate(coord);
				/*	binasStationView.getCoordinate().setX( stationSView.getCoordinate().getX());
					binasStationView.getCoordinate().setY( stationSView.getCoordinate().getY());*/
					
					binasStationView.setTotalGets( stationSView.getTotalGets());
					binasStationView.setTotalReturns(stationSView.getTotalReturns());
					
					binasStationView.setAvailableBinas(stationSView.getAvailableBinas() );
					binasStationView.setFreeDocks(stationSView.getFreeDocks() );
					
					return binasStationView;
				} catch (StationClientException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
		return null;
	}

	@Override
	public int getCredit(String email) throws UserNotExists_Exception {
		boolean validEmail = User.checkEmail(email);
		
		if(validEmail == true) {
			/* Vai buscar o utilizador que tem este email */
			User utilizador = BinasManager.getInstance().getUserById(email);
			/* Devolve o credito do utilizador */
			int credito = utilizador.getCredit();
			
			return credito;
		}
		else {
			UserNotExists ex = new UserNotExists();
			throw new UserNotExists_Exception(email, ex);
		}
	}

	@Override
	public UserView activateUser(String email) throws EmailExists_Exception, InvalidEmail_Exception {
		
		/* Cria um novo atualizador e adiciona a um map com user's */
		User utilizador = BinasManager.getInstance().putUser(email);
		/* Cria um UserView com as informaçoes do user que se criou */
		UserView userView = new UserView();
		userView.setEmail(utilizador.getEmail());
		userView.setHasBina(utilizador.getHasBina());
		userView.setCredit(utilizador.getCredit());
		
		return userView;
	}

	@Override
	public void rentBina(String stationId, String email) throws AlreadyHasBina_Exception, InvalidStation_Exception,
			NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception {
		
		/* Cria um novo atualizador e adiciona a um map com user's */
		try {
			User utilizador;
			try {
				utilizador = BinasManager.getInstance().putUser(email);
				
				/* Ve se tem credito */
				if(utilizador.getCredit() <= 0){
					NoCredit e = new NoCredit();
					throw new NoCredit_Exception("Not enogth credit", e);
				}
				/* Ve se ja tem outra bicicleta alugada */
				if(utilizador.getHasBina() == true){
					AlreadyHasBina e = new AlreadyHasBina();
					throw new AlreadyHasBina_Exception("Already Has Bina: ", e);
				}
				
			} catch (EmailExists_Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
		} 
		catch (InvalidEmail_Exception e) {
			e.printStackTrace();
		}
		
		StationView binasStationView = null;
		Collection<UDDIRecord> stations = getStations();
		
		for(UDDIRecord stationX: stations ) {
			if(stationId == stationX.getOrgName()) {
				try {
					StationClient sClient = new StationClient ( stationX.getUrl() , stationX.getOrgName());
					
					try {
						sClient.getBina();
					} catch (org.binas.station.ws.NoBinaAvail_Exception e) {
						NoBinaAvail ex = new NoBinaAvail();
						throw new NoBinaAvail_Exception("No bina avail", ex);
						//e.printStackTrace();
					}

				} 
				catch (StationClientException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		/* Cria um novo atualizador e adiciona a um map com user's */
		try {
			User utilizador;
			try {
				utilizador = BinasManager.getInstance().putUser(email);
				
				utilizador.setCredit(utilizador.getCredit()-1);
				/* poe que o cliente ja tem uma bicicleta */
				utilizador.setHasBina(true);
			} catch (EmailExists_Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		} 
		catch (InvalidEmail_Exception e) {
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void returnBina(String stationId, String email)
			throws FullStation_Exception, InvalidStation_Exception, NoBinaRented_Exception, UserNotExists_Exception {
		
		/* Cria um novo atualizador e adiciona a um map com user's */
		try {
			try {
				User utilizador = BinasManager.getInstance().putUser(email);
			} catch (EmailExists_Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		catch (InvalidEmail_Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		StationView binasStationView = null;
		Collection<UDDIRecord> stations = getStations();
		
		for(UDDIRecord stationX: stations ) {
			if(stationId == stationX.getOrgName()) {
				try {
					StationClient sClient = new StationClient ( stationX.getUrl() , stationX.getOrgName());
					
					
					try {
						int bonus = 0;
						bonus = sClient.returnBina();
						
						try {
							User utilizador;
							try {
								utilizador = BinasManager.getInstance().putUser(email);
								
								utilizador.setCredit(utilizador.getCredit()+bonus);
								utilizador.setHasBina(false);
							} catch (EmailExists_Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						} 
						catch (InvalidEmail_Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (NoSlotAvail_Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					

				} 
				catch (StationClientException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

	@Override
	public String testPing(String inputMessage){
		
		StringBuilder resultTestPing = new StringBuilder();
		System.out.println("Iniciating Binas-ws Test Ping");
		
		/* Get Stations */
		Collection<UDDIRecord> stations = getStations();
		for(UDDIRecord stationX: stations ) {
			//System.out.println(stationX);
			try{
				StationClient stationClient = new StationClient(stationX.getUrl());
				resultTestPing.append(stationClient.testPing(inputMessage)).append("\n");
			}
			catch(StationClientException ex){
	    		System.out.println("stationClient expection in test ping:");
	    		return null;
			}
			
		}
		
		return resultTestPing.toString();
	}

	@Override
	public void testClear() {
		BinasManager.getInstance().clean();
		
	}

	@Override
	public void testInitStation(String stationId, int x, int y, int capacity, int returnPrize) throws BadInit_Exception {
		Collection<UDDIRecord> stations = getStations();
		
		for(UDDIRecord stationX: stations ) {
			if(stationId == stationX.getOrgName()) {
				try {
					StationClient sClient = new StationClient ( stationX.getUrl() , stationX.getOrgName());
					if(sClient != null) {
						try {
							sClient.testInit(x, y, capacity, returnPrize);
						} 
						catch (org.binas.station.ws.BadInit_Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
					}
				}
				catch (StationClientException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
	}


	@Override
	public void testInit(int userInitialPoints) throws BadInit_Exception {
		BinasManager.getInstance().init(userInitialPoints);
		
	}
	
	/* Poem todas as estações(nome e wsUrl) numa lista */
	public Collection<UDDIRecord> getStations(){
		Collection<UDDIRecord> stations = null;
    	try{
    		UDDINaming uddi = endpointManager.getUddiNaming();
    		//System.out.println(uddi);
    		stations = uddi.listRecords("A30_Station%");
    	}
    	catch(UDDINamingException e){
    		System.out.println("Could not list suppliers");
    		return null;
    	}
    	
    	return stations;
	}
}
