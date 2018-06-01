package org.binas.ws.it;

import org.binas.ws.AlreadyHasBina_Exception;
import org.binas.ws.BadInit_Exception;
import org.binas.ws.FullStation_Exception;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.NoBinaAvail_Exception;
import org.binas.ws.NoBinaRented_Exception;
import org.binas.ws.NoCredit_Exception;
import org.binas.ws.StationView;
import org.binas.ws.UserNotExists_Exception;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.junit.Assert;

public class RentBinaIT extends BaseIT  {
	String stationName = "A30_Station1";
	String email = "aaa@aaa";
	String email2 = "bbb@aaa";
	


	@Test
	public void successRent() throws 	BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception,
									 	NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, 
									 	FullStation_Exception, NoBinaRented_Exception {
		binasClient.testInitStation(stationName ,1,1, 10, 10);
		binasClient.rentBina(stationName, email);

		StationView station = binasClient.getInfoStation(stationName);

		Assert.assertEquals(station.getTotalGets() , 0);		
		Assert.assertEquals(station.getTotalReturns() , 0);	
		Assert.assertEquals(station.getAvailableBinas() , 20);		
		Assert.assertEquals(station.getFreeDocks() , 0);	
	}
	
	@Test
	public void rentTwice() throws 	BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception,
									 	NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, 
									 	FullStation_Exception, NoBinaRented_Exception {
		binasClient.testInitStation(stationName ,1,1, 10, 10);
		binasClient.rentBina(stationName, email);
		binasClient.rentBina(stationName, email);
	}
	
	@Test
	public void rentTwoWithOnlyOne() throws 	BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception,
									 	NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, 
									 	FullStation_Exception, NoBinaRented_Exception {
		binasClient.testInitStation(stationName ,1,1, 1, 10);
		binasClient.rentBina(stationName, email);
		binasClient.rentBina(stationName, email2);
	}
	
	@Test
	public void rentNameNull() throws 	BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception,
									 	NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, 
									 	FullStation_Exception, NoBinaRented_Exception {
		binasClient.rentBina(null, email);
	}
	
	@Test
	public void rentNameNoExists() throws 	BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception,
									 	NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, 
									 	FullStation_Exception, NoBinaRented_Exception {
		binasClient.rentBina("stationName", email);
	}
	
	@Test
	public void rentNameEmpty() throws 	BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception,
									 	NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, 
									 	FullStation_Exception, NoBinaRented_Exception {
		binasClient.rentBina("", email);
	}
	
	@Test
	public void rentEmailNull() throws 	BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception,
									 	NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, 
									 	FullStation_Exception, NoBinaRented_Exception {
		binasClient.rentBina(stationName, null);
	}
	
	@Test
	public void rentEmailEmpty() throws 	BadInit_Exception, AlreadyHasBina_Exception, InvalidStation_Exception,
									 	NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception, 
									 	FullStation_Exception, NoBinaRented_Exception {
		binasClient.rentBina(stationName, "");
	}
	@After
	public void tearDown() {
		binasClient.testClear();
	}
}
