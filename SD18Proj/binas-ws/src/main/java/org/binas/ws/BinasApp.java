package org.binas.ws;

public class BinasApp {

	public static void main(String[] args) throws Exception {
		//System.out.println(BinasApp.class.getSimpleName() + " running");
		// TODO

		
		// Check arguments
		if (args.length < 3) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " +BinasApp.class.getName() + "wsName wsURL OR wsName wsURL uddiURL");
			return;
		}
		String wsName = args[0];
		String wsURL = args[1];
		String uddiURL = args[2];
		
		// TODO handle UDDI arguments

		BinasEndpointManager endpoint = new BinasEndpointManager(uddiURL, wsName, wsURL);
		//Station.getInstance().setId(wsName);

		System.out.println(BinasApp.class.getSimpleName() + " running");

		// TODO start Web Service
		try {
			endpoint.start();
			endpoint.awaitConnections();
		} 
		finally {
			endpoint.stop();
		}

	}

}