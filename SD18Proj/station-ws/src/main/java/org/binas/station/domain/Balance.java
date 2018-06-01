package org.binas.station.domain;

import java.util.concurrent.atomic.AtomicInteger;

/** 
 * 
 * Class that store tag & balance.
 * 
 * 
 */
public class Balance {
	
	private AtomicInteger balance;
	private AtomicInteger tag;
	
	
	public Balance(AtomicInteger balance, AtomicInteger tag) {
		super();
		this.balance = balance;
		this.tag = tag;
	}
	
	public AtomicInteger getBalance() {
		return balance;
	}
	
	public void setBalance(AtomicInteger balance) {
		this.balance = balance;
	}
	
	public AtomicInteger getTag() {
		return tag;
	}

	public void setTag(AtomicInteger tag) {
		this.tag = tag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((balance == null) ? 0 : balance.hashCode());
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Balance other = (Balance) obj;
		if (balance == null) {
			if (other.balance != null)
				return false;
		} else if (!balance.equals(other.balance))
			return false;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		return true;
	}
	
	
	
}
