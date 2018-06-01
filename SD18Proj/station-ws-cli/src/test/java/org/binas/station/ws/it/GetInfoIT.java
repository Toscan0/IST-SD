package org.binas.station.ws.it;

import org.binas.station.ws.NoBinaAvail_Exception;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.StationView;
import org.junit.Test;
import org.junit.After;
import org.junit.Assert;

public class GetInfoIT extends BaseIT{
	
	@Test
	public void dezCapacity() throws BadInit_Exception {
		client.testInit(1,1, 10, 10);
		
		//check
		//cria cliente
		StationView station_view;
		
		try {
			//aluga 10 bicicletas
			for(int i = 0; i <= 9; i++) {
				client.getBina();
			}
			//cliente depois de ter alugado
			station_view = client.getInfo();
			
			Assert.assertEquals(station_view.getCoordinate().getX(), 1);
			Assert.assertEquals(station_view.getCoordinate().getY(), 1);
			Assert.assertEquals(station_view.getCapacity(), 10);
			
			Assert.assertEquals(station_view.getTotalGets() , 10);
			Assert.assertEquals(station_view.getFreeDocks() , 10);
			
			//assumimos que nenhuma bicicleta foi reposta 
			Assert.assertEquals(station_view.getTotalReturns(), 0);
			
			Assert.assertEquals(station_view.getAvailableBinas(), 0);
		}
		catch(NoBinaAvail_Exception ex) {
			Assert.fail();
		}
	
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
			//aluga 10 bicicletas
			for(int i = 0; i <= 9; i++) {
				client.getBina();
			}
			//cliente depois de ter alugado
			station_view = client.getInfo();
			
			Assert.assertEquals(station_view.getCoordinate().getX(), 1);
			Assert.assertEquals(station_view.getCoordinate().getY(), 1);
			Assert.assertEquals(station_view.getCapacity(), 200);
			
			Assert.assertEquals(station_view.getTotalGets() , 10);
			Assert.assertEquals(station_view.getFreeDocks() , 10);
			
			//assumimos que nenhuma bicicleta foi reposta 
			Assert.assertEquals(station_view.getTotalReturns(), 0);
			
			Assert.assertEquals(station_view.getAvailableBinas(), 190);
		}
		catch(NoBinaAvail_Exception ex) {
			Assert.fail();
		}
	
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