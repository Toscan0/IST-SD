package org.binas.ws.it;

import org.binas.ws.BadInit_Exception;
import org.junit.Test;

public class TestInitStationIT extends BaseIT{
	private String stationName ="A30_Station";
	
	@Test
	public void setUp1() throws BadInit_Exception   {
		binasClient.testInitStation(stationName, 1, 1, 10, 10);
		binasClient.testInitStation(stationName + "1", 10, 10, 10, 10);
		binasClient.testInitStation(stationName + "2", 20, 20, 10, 10);
	}
	
	@Test
	public void setUp2() throws BadInit_Exception   {
		binasClient.testInit(10);
		binasClient.testInit(5);
	}
	
	@Test
	public void setUp3() throws BadInit_Exception   {
		binasClient.testClear();
	}
	
}
