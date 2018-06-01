package org.binas.domain;

import java.util.ArrayList;
import java.util.HashMap;

import org.binas.domain.User;
import org.binas.ws.EmailExists;
import org.binas.ws.EmailExists_Exception;
import org.binas.ws.InvalidEmail;
import org.binas.ws.InvalidEmail_Exception;

public class BinasManager {
	private HashMap<String, User> mapUser = new HashMap<String, User>();
	private static final int DEFAULT_CREDIT = 10;
	
	private int userCredit = 10;
	// Singleton -------------------------------------------------------------

	private BinasManager() {
	}

	public HashMap<String, User> getMapUser() {
		return mapUser;
	}


	public void setMapUser(HashMap<String, User> mapUser) {
		this.mapUser = mapUser;
	}


	public int getUserCredit() {
		return userCredit;
	}


	public void setUserCredit(int userCredit) {
		this.userCredit = userCredit;
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

	
	/* Cria o user e adiciona o ao map*/
	public User putUser(String email) throws InvalidEmail_Exception, EmailExists_Exception{
		boolean validEmail = User.checkEmail(email);
		if(validEmail == false) {
			InvalidEmail e = new InvalidEmail();
			throw new InvalidEmail_Exception("email invalido", e);
		}
		/* ve se o cliente ja existe */ 
		if(mapUser.containsKey(email) == true) {
			EmailExists e = new EmailExists();
			throw new EmailExists_Exception("email invalido", e);
		}
		/* false- um user criado nao tem bina */
		User utilizador = new User(email, false, this.getUserCredit());
		mapUser.put(email, utilizador);
		
		return utilizador;
	}
	
	/* Recebe um email e devolve o user respetivo */
	public User getUserById(String email){
		User utilizador = mapUser.get(email);
		
		return utilizador;
	}
	
	/* Inicializa o mapa */
	public void init(int userInitialPoints){
		userCredit = userInitialPoints;
	}
	
	/* Limpa o mapa */
	public void clean() {
		mapUser.clear();
		userCredit = DEFAULT_CREDIT;
	}
}
