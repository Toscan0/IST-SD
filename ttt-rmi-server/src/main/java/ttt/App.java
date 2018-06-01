package ttt;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class App {
	public static final int registryPort = 1099;
	
	public static void main(String args[]){
    
        System.out.println("Main OK");
        try{
        	TTT ttt = new TTT();

            final Registry reg = LocateRegistry.createRegistry(registryPort);
            reg.rebind("Registogame", ttt);

            // A more realistic would be having an autonomous RMI Registry
            // available at the default port
            // (implies defining a 'codebase' to allow the RMI Registry
            // to remotely obtain the interfaces for the
            // objects that will be registered):
            //
            // Naming.rebind("ShapeList", aShapelist);
/*
            System.out.println("ShapeList server ready");

            System.out.println("Awaiting connections");
            System.out.println("Press enter to shutdown");
            */
            
            System.in.read();
            System.exit(0);

        }catch(Exception e) {
            System.out.println("ShapeList server main " + e.getMessage());
            System.exit(0);
        }
    }
}
