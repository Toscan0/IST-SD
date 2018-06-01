package org.binas.ws.it;

import java.util.Collection;

import org.binas.ws.AlreadyHasBina_Exception;
import org.binas.ws.BadInit_Exception;
import org.binas.ws.CoordinatesView;
import org.binas.ws.FullStation_Exception;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.NoBinaAvail_Exception;
import org.binas.ws.NoBinaRented_Exception;
import org.binas.ws.NoCredit_Exception;
import org.binas.ws.StationView;
import org.binas.ws.UserNotExists_Exception;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class ReturnBinaIT extends BaseIT  {
	String stationName = "A30_Station1";
	String email = "aaa@aaa";
	String email2 = "bbb@aaa";
	

	@Test
	public void successReturn() throws 	BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception,
									 	NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, 
									 	FullStation_Exception, NoBinaRented_Exception {
		binasClient.testInitStation(stationName ,1,1, 10, 10);
		binasClient.rentBina(stationName, email);
		binasClient.returnBina(stationName, email);
		StationView station = binasClient.getInfoStation(stationName);

		Assert.assertEquals(station.getTotalGets() , 0);
		Assert.assertEquals(station.getTotalReturns() , 0);
	}
	
	@Test
	public void returnTwice() throws 	BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception,
									 	NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, 
									 	FullStation_Exception, NoBinaRented_Exception {
		binasClient.testInitStation(stationName ,1,1, 10, 10);
		binasClient.rentBina(stationName, email);
		binasClient.returnBina(stationName, email);
		binasClient.returnBina(stationName, email);
	}
	
	
	@Test
	public void rentTwoWithOnlyOne() throws 	BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception,
									 	NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, 
									 	FullStation_Exception, NoBinaRented_Exception {
		binasClient.testInitStation(stationName ,1,1, 1, 10);
		binasClient.returnBina(stationName, email);
		binasClient.returnBina(stationName, email2);
	}
	
	@Test
	public void returnNameNull() throws 	BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception,
									 	NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, 
									 	FullStation_Exception, NoBinaRented_Exception {
		binasClient.returnBina(null, email);
	}
	
	@Test
	public void returnNameNoExists() throws 	BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception,
									 	NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, 
									 	FullStation_Exception, NoBinaRented_Exception {
		binasClient.returnBina("stationName", email);
	}
	
	@Test
	public void returnNameEmpty() throws 	BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception,
									 	NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, 
									 	FullStation_Exception, NoBinaRented_Exception {
		binasClient.returnBina("", email);
	}
	
	@Test
	public void returnEmailNull() throws 	BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception,
									 	NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, 
									 	FullStation_Exception, NoBinaRented_Exception {
		binasClient.returnBina(stationName, null);
	}
	
	@Test
	public void returnEmailEmpty() throws 	BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception,
									 	NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, 
									 	FullStation_Exception, NoBinaRented_Exception {
		binasClient.returnBina(stationName, "");
	}
	@After
	public void tearDown() {
		binasClient.testClear();
	}
}
