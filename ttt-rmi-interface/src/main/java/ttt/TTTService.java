package ttt;

import java.rmi.Remote;
import java.rmi.RemoteException;

/** Tic Tac Toe game remote interface. */
public interface TTTService extends Remote {
    /*
     *	Shape newShape(GraphicalObject g) throws RemoteException;
    	
    	Vector allShapes() throws RemoteException;
    	
    	int getVersion() throws RemoteException;
     * 
     */
    /* TO-DO */
	
	boolean playRandom(int player) throws RemoteException;
	
	String currentBoard() throws RemoteException;
     
    boolean play(int row, int column, int player) throws RemoteException;
     
    int checkWinner() throws RemoteException;

}
