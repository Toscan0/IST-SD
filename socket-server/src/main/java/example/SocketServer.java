package example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


public class SocketServer 
{
    public static void main( String[] args ) throws IOException {
        // Check arguments
        if (args.length < 1) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s port%n", SocketServer.class.getName());
            return;
        }

        // Convert port from String to int
        int port = Integer.parseInt(args[0]);

        // Create server socket
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.printf("Server accepting connections on port %d %n", port);

        // wait for and then accept client connection
        // a socket is created to handle the created connection
        Socket clientSocket = serverSocket.accept();
        System.out.printf("Connected to client %s on port %d %n",
            clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());

        // Create stream to receive data from client
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        // Receive data until client closes the connection
        String response;
        while (true) {
        	//Reads a line of text. 
        	//A line ends with a line feed ('\n').
        	response = in.readLine();
        	if(response == null) {
        		break;
        	}
        	String numeros[] = response.split(" ");
        	String msg;
        	int x= Integer.parseInt(numeros[0]);
        	int y= Integer.parseInt(numeros[1]);
        	int z= Integer.parseInt(numeros[2]);
        	if((x*y) < z){
        		msg = "ok";
        	}
        	else{
        		msg = "error";
        	}
        	/**
        	System.out.printf(msg);
        	System.out.printf("\n");**/
        	/*****Codigo para o lab*****/
        	// Create stream to send data to client
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            // Send text to client as bytes
            String text = "message recived, over.";
            out.writeBytes(msg);
            out.writeBytes("\n");
            System.out.println("Sent text: " + msg);
            /*****End*****/
        }
        

        // Close connection to current client
        clientSocket.close();
        System.out.println("Closed connection with client");

        // Close server socket
        serverSocket.close();
        System.out.println("Closed server socket");
    }
}
