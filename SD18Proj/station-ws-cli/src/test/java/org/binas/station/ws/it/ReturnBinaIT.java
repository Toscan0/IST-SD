package org.binas.station.ws.it;

import org.binas.station.ws.NoBinaAvail_Exception;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.StationView;
import org.junit.Test;
import org.junit.After;
import org.junit.Assert;

public class ReturnBinaIT extends BaseIT{
	
	@Test
	public void dezCapacity() throws BadInit_Exception {
		client.testInit(1,1, 10, 10);
		
		//check
		//cria cliente
		StationView station_view;
	
		try {
			int returnPrize = client.returnBina();
			station_view = client.getInfo();
			
			Assert.assertEquals(returnPrize, 10);
			Assert.assertEquals(station_view.getFreeDocks(),  9);	
		}
		catch(NoSlotAvail_Exception ex) {
			Assert.fail();
		}
		
	}
	
	@Test
	public void duzentosCapacity() throws BadInit_Exception {
		client.testInit(1,1, 200, 10);
		
		//check
		//cria cliente
		StationView station_view;
	
		try {
			int returnPrize = client.returnBina();
			station_view = client.getInfo();
			
			Assert.assertEquals(returnPrize, 10);
			Assert.assertEquals(station_view.getFreeDocks(),  9);	
		}
		catch(NoSlotAvail_Exception ex) {
			Assert.fail();
		}
	}
	
	@After
	public void tearDown() {
		client.testClear();
	}
}