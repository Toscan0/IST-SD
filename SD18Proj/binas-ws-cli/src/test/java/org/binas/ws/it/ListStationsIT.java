package org.binas.ws.it;

import org.junit.Test;

import java.util.List;

import org.binas.ws.BadInit_Exception;
import org.binas.ws.CoordinatesView;
import org.binas.ws.StationView;
import org.binas.ws.cli.BinasClientException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

public class ListStationsIT extends BaseIT {
	CoordinatesView coord = new CoordinatesView();
	String stationName = "A30_Station";
	
	@Before
	public void setUp() throws BadInit_Exception   {
		binasClient.testInitStation(stationName + "3", 1, 1, 10, 10);
		binasClient.testInitStation(stationName + "1", 10, 10, 10, 10);
		binasClient.testInitStation(stationName + "2", 20, 20, 10, 10);
		coord.setX(1);
		coord.setY(1);
		
	}
	/*@Test
	public void nullCoord() throws BadInit_Exception   {
		binasClient.testInitStation(stationName + "3", 1, 1, 10, 10);
		
	}*/
	
	//listStations(Integer numberOfStations, CoordinatesView coordinates)//
	/*@Test
	public void nullCoord()   {
		//binasClient.testInitStation(stationName, 1, 1, 10, 10);
		binasClient.listStations(3, null);
	}	
	
	@Test
	public void allNull()   {
		//binasClient.testInitStation(stationName, 1, 1, 10, 10);
		binasClient.listStations(null, null);
	}*/
	
		
	@Test
	public void OneMoreStation()   {
		List<StationView> stView = binasClient.listStations(2, coord);
		Assert.assertNotNull(stView);
	}
		
		
	@After
	public void tearDown() {
		binasClient.testClear();
	}
}	
