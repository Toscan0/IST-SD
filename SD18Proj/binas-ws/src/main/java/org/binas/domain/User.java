package org.binas.domain;

/** Class to store map coordinates. */
public class User {
	
	private String email;
	private Boolean hasBina;
	private Integer credit;
	
	public User(String email, Boolean hasBina, Integer credit) {
		this.email = email;
		this.hasBina = hasBina;
		this.credit = credit;
	}

	public Boolean getHasBina() {
		return this.hasBina;
	}

	public void setHasBina(Boolean hasBina) {
		this.hasBina = hasBina;
	}
	
	public Integer getCredit() {
		return this.credit;
	}

	public void setCredit(Integer credit) {
		this.credit = credit;
	}
	
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	
	public static boolean checkEmail(String email) {
		if(email == null) {
			return false;
		}
		
		String userEmail = email; 
		String[] parts = userEmail.split("@");
		if(parts.length !=  2) {
			return false;
		}
		
		String utilizador = parts[0];
		String dominio = parts[1];
		if(utilizador.trim().equals("") || dominio.trim().equals("")) {
			return false;
		}
		
		/*
		 * Fix me
		 */
		
		return true;
	}
	
}
