package org.binas.station.ws;

import javax.jws.WebService;

import org.binas.station.domain.Coordinates;
import org.binas.station.domain.Station;
import org.binas.station.domain.exception.BadInitException;
import org.binas.station.domain.exception.NoBinaAvailException;
import org.binas.station.domain.exception.NoSlotAvailException;

/**
 * This class implements the Web Service port type (interface). The annotations
 * below "map" the Java class to the WSDL definitions.
 */
// TODO


// endpoint interface (nome do tipo Java do PortType)
// wsdlLocation (nome do ficheiro WSDL)   wsdlLocation = "...",
// name (definido no WSDL)
// portName (WSDL) "...Port",
//  targetNamespace (WSDL) "...",
// serviceName (WSDL) "...Service"

@WebService(endpointInterface = "org.binas.station.ws.StationPortType",
	wsdlLocation = "station.1_0.wsdl",
	name ="StationWebService",
	portName = "StationPort",
	targetNamespace= "http://ws.station.binas.org/",
	serviceName = "StationService")
public class StationPortImpl implements StationPortType {

	/**
	 * The Endpoint manager controls the Web Service instance during its whole
	 * lifecycle.
	 */
	private StationEndpointManager endpointManager;

	/** Constructor receives a reference to the endpoint manager. */
	public StationPortImpl(StationEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}

	// Main operations -------------------------------------------------------

	/** Retrieve information about station. */
	@Override
	public StationView getInfo() {
	// TODO
		StationView station_view = new StationView();
		
		//Instancia da estação
		Station station_instance = Station.getInstance();
		
		//Ve quantas bibliotecas estao disponiveis-> define na view
		station_view.setAvailableBinas(station_instance.getAvailableBinas());
		//Ve a capacidade maxima da estacao->define na view
		station_view.setCapacity(station_instance.getMaxCapacity());
		
		//coordeadas da estacao
		Coordinates station_coordinates = station_instance.getCoordinates();
		CoordinatesView coordinates_view = new CoordinatesView();
		coordinates_view.setX(station_coordinates.getX());
		coordinates_view.setY(station_coordinates.getY());
		//atribui a view as coordenadas da estacao
		station_view.setCoordinate(coordinates_view);

		//numero de vagas
		station_view.setFreeDocks(station_instance.getFreeDocks());
		//id da estacao
		station_view.setId(station_instance.getId());
		
		station_view.setTotalGets(station_instance.getTotalGets());
		station_view.setTotalReturns(station_instance.getTotalReturns());
		
		//station_view.setBonus(station_instance.getBonus());
		return station_view;
	}
	
	/** Return a bike to the station. */
	@Override
	public int returnBina() throws NoSlotAvail_Exception {
	// TODO
		try {
			return Station.getInstance().returnBina();
		}
		catch(NoSlotAvailException ex) {
			throw new NoSlotAvail_Exception("No slot to bike avilable in that station", new NoSlotAvail());
			}
	}
	
	
	/** Take a bike from the station. */
	@Override
	public void getBina() throws NoBinaAvail_Exception {
	// TODO
		try {
			Station.getInstance().getBina();
		}
		catch(NoBinaAvailException ex) {
			throw new NoBinaAvail_Exception("No Bina available in that station", new NoBinaAvail());
		}
	}

	// Test Control operations -----------------------------------------------

	/** Diagnostic operation to check if service is running. */
	@Override
	public String testPing(String inputMessage) {
		//If no input is received, return a default name.
		if (inputMessage == null || inputMessage.trim().length() == 0)
			inputMessage = "friend";
		// If the station does not have a name, return a default.
		String wsName = endpointManager.getWsName();
		if (wsName == null || wsName.trim().length() == 0)
			wsName = "Station";
		// Build a string with a message to return.
		StringBuilder builder = new StringBuilder();
		builder.append("Hello ").append(inputMessage);
		builder.append(" from ").append(wsName);
		return builder.toString();
	}
	
	/** Return all station variables to default values. */
	@Override
	public void testClear() {
		Station.getInstance().reset();
	}
	
	/** Set station variables with specific values. */
	@Override
	public void testInit(int x, int y, int capacity, int returnPrize) throws BadInit_Exception {
		try {
			Station.getInstance().init(x, y, capacity, returnPrize);
		} catch (BadInitException e) {
			//changed
			throw new BadInit_Exception("", new BadInit());
		}
	}

	// View helpers ----------------------------------------------------------

	/** Helper to convert a domain station to a view. */
	//@Override
	private StationView buildStationView(Station station) {
		StationView view = new StationView();
		view.setId(station.getId());
		view.setCoordinate(buildCoordinatesView(station.getCoordinates()));
		view.setCapacity(station.getMaxCapacity());
		view.setTotalGets(station.getTotalGets());
		view.setTotalReturns(station.getTotalReturns());
		view.setFreeDocks(station.getFreeDocks());
		view.setAvailableBinas(station.getAvailableBinas());
		return view;
	}
	
	/** Helper to convert a domain coordinates to a view. */
	//@Override
	private CoordinatesView buildCoordinatesView(Coordinates coordinates) {
		CoordinatesView view = new CoordinatesView();
		view.setX(coordinates.getX());
		view.setY(coordinates.getY());
		return view;
	}

	// Exception helpers -----------------------------------------------------

	/** Helper to throw a new NoBinaAvail exception. */
	//@Override
	private void throwNoBinaAvail(final String message) throws
		NoBinaAvail_Exception {
		NoBinaAvail faultInfo = new NoBinaAvail();
		faultInfo.message = message;
		throw new NoBinaAvail_Exception(message, faultInfo);
	}
	
	/** Helper to throw a new NoSlotAvail exception. */
	//@Override
	private void throwNoSlotAvail(final String message) throws NoSlotAvail_Exception {
		NoSlotAvail faultInfo = new NoSlotAvail();
		faultInfo.message = message;
		throw new NoSlotAvail_Exception(message, faultInfo);
	}
	
	/** Helper to throw a new BadInit exception. */
	//@Override
	private void throwBadInit(final String message) throws BadInit_Exception {
		BadInit faultInfo = new BadInit();
		faultInfo.message = message;
		throw new BadInit_Exception(message, faultInfo);
	}
}
