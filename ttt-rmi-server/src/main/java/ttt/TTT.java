package ttt;

import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.Vector;
import java.util.Random;

/**
 * TTT - Tic Tac Toe.
 */
@SuppressWarnings("serial")
public class TTT extends UnicastRemoteObject implements TTTService{
	
	
	public TTT() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public TTT(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
		super(port, csf, ssf);
		// TODO Auto-generated constructor stub
	}

	public TTT(int port) throws RemoteException {
		super(port);
		// TODO Auto-generated constructor stub
	}

	/** The Game Board */
	private char board[][] = {
			{ '1', '2', '3' }, /* Initial values are reference numbers */
			{ '4', '5', '6' }, /* used to select a vacant square for */
			{ '7', '8', '9' } /* a turn. */
	};
	
	/** Next player */
	private int nextPlayer = 0;
	/** Number of plays */
	private int numPlays = 0;
	
	public boolean playRandom(int player) {
		System.out.println("************");
		Vector possiblePlay = new Vector();
		
		for(int i = 0; i <= 2 ; i++){
			for(int e = 0; e <= 2 ; e++){
				if(board[i][e] != 'X' || board[i][e] != 'O') { 
					possiblePlay.add(board[i][e]);
				}
			}
		}
		
		Random rand = new Random(); 
		int pickedPlay = rand.nextInt(possiblePlay.size());
		System.out.println(pickedPlay);
		if(pickedPlay == 1) {
			play(0 , 0,player);
		}
		if(pickedPlay == 2) {
			play(0 , 1,player);
		}
		if(pickedPlay == 3) {
			play(0 , 2,player);
		}
		if(pickedPlay == 4) {
			play(1 , 0,player);
		}
		if(pickedPlay == 5) {
			play(1 , 1,player);	
		}
		if(pickedPlay == 6) {
			play(1 , 2,player);
		}
		if(pickedPlay == 7) {
			play(2 , 0,player);
		}
		if(pickedPlay == 8) {
			play(2 , 1,player);
		}
		if(pickedPlay == 9) {
			play(2 , 2,player);
		}
		possiblePlay.clear();
		return true;
	}
	
	
	
	/** Return a textual representation of the current game board. */
	public String currentBoard() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n\n ");

		// acquire lock for current object
		synchronized (this) {
			sb.append(board[0][0]).append(" | ");
			sb.append(board[0][1]).append(" | ");
			sb.append(board[0][2]).append(" ");
			sb.append("\n---+---+---\n ");
			sb.append(board[1][0]).append(" | ");
			sb.append(board[1][1]).append(" | ");
			sb.append(board[1][2]).append(" ");
			sb.append("\n---+---+---\n ");
			sb.append(board[2][0]).append(" | ");
			sb.append(board[2][1]).append(" | ");
			sb.append(board[2][2]).append(" \n");
		}
		// release lock

		return sb.toString();
	}

	/** Make a game play on behalf of provided player. */
	public boolean play(int row, int column, int player) {
		// outside board ?
		if (!(row >= 0 && row < 3 && column >= 0 && column < 3))
			return false;

		// lock
		synchronized (this) {
			// invalid square ?
			if (board[row][column] > '9')
				return false;
			// not player's turn ?
			if (player != nextPlayer)
				return false;
			// no more plays left ?
			if (numPlays == 9)
				return false;

			/* insert player symbol */
			board[row][column] = (player == 1) ? 'X' : 'O';
			nextPlayer = (nextPlayer + 1) % 2;
			numPlays++;
			return true;
		}
		// unlock on return

	}

	/**
	 * Check if there is a game winner. Synchronized keyword means that the lock
	 * of the object is acquired when the method is called and released on
	 * return.
	 */
	public synchronized int checkWinner() {
		int i;

		/* Check for a winning line - diagonals first */
		if ((board[0][0] == board[1][1] && board[0][0] == board[2][2])
				|| (board[0][2] == board[1][1] && board[0][2] == board[2][0])) {
			if (board[1][1] == 'X')
				return 1;
			else
				return 0;
		} else {
			/* Check rows and columns for a winning line */
			for (i = 0; i <= 2; i++) {
				if ((board[i][0] == board[i][1] && board[i][0] == board[i][2])) {
					if (board[i][0] == 'X')
						return 1;
					else
						return 0;
				}

				if ((board[0][i] == board[1][i] && board[0][i] == board[2][i])) {
					if (board[0][i] == 'X')
						return 1;
					else
						return 0;
				}
			}
		}

		if (numPlays == 9)
			/* A draw! */
			return 2;
		else
			/* Game is not over yet */
			return -1;
	}

}
