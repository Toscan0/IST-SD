package org.binas.ws.it;

import java.util.Collection;


import org.binas.ws.BadInit_Exception;
import org.binas.ws.EmailExists_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.StationView;
import org.binas.ws.UserView;
import org.junit.Test;
import org.junit.After;
import org.junit.Assert;

public class GetInfoStationIT extends BaseIT {
	private String stationName = "A30_Station1";
	
	
	@Test
	public void succes1() throws BadInit_Exception, EmailExists_Exception, InvalidEmail_Exception, InvalidStation_Exception{
		binasClient.testInitStation(stationName, 1, 1, 10, 10);
		StationView station = binasClient.getInfoStation(stationName);
		Assert.assertEquals(station.getId(), stationName);
	}
	
	@Test
	public void succes2() throws BadInit_Exception, EmailExists_Exception, InvalidEmail_Exception, InvalidStation_Exception{
		binasClient.testInitStation(stationName, 1,6, 30, 10);
		StationView station = binasClient.getInfoStation(stationName);
		Assert.assertEquals(station.getId(), stationName);
		
	}
	
/*	@Test(expected = InvalidStation_Exception.class)
	public void failure() throws BadInit_Exception, EmailExists_Exception, InvalidEmail_Exception, InvalidStation_Exception{
		binasClient.testInitStation(stationName, 1, 1, 10, 10);
		StationView station = binasClient.getInfoStation(null);
		
	}
	*/
	
	
	@After
	public void tearDown() {
		binasClient.testClear();
	}


}
