package ttt;
 
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;
 
/** This is the client of the Tic Tac Toe game. */

//Cliente

public class Game {
     
    TTTService game;
    Scanner keyboardSc;
    int winner = 0;
    int player = 1;
 
    public Game() throws MalformedURLException, RemoteException, NotBoundException{
        game = (TTTService) Naming.lookup("Registogame");
        keyboardSc = new Scanner(System.in);
    }
 
    public int readPlay() {
        int play;
        do {
            System.out.printf(
                    "\nPlayer %d, please enter the number of the square "
                            + "where you want to place your %c (or 0 to refresh the board): \n\n",
                    player, (player == 1) ? 'X' : 'O');
            play = keyboardSc.nextInt();
        } while ((play > 9 || play < 0) && play != 10);
        return play;
    }
 
    public void playGame() throws RemoteException{
        int play;
        boolean playAccepted;
 
        do {
            player = ++player % 2;
            do {
                System.out.println(game.currentBoard());
                play = readPlay();
                if (play != 0 && play != 10) {
                    playAccepted = game.play(--play / 3, play % 3, player);
                    if (!playAccepted)
                        System.out.println("Invalid play! Try again.");
                }
                else if(play == 10) {
                	playAccepted = game.playRandom(player);
                }
                else {
                    playAccepted = false;
                }
            } while (!playAccepted);
            winner = game.checkWinner();
        } while (winner == -1);
        System.out.println(game.currentBoard());
    }
 
    public void congratulate() {
        if (winner == 2)
            System.out.printf("\nHow boring, it is a draw\n");
        else
            System.out.printf("\nCongratulations, player %d, YOU ARE THE WINNER!\n", winner);
    }
 
     
    /** The program starts running in the main method. */
    public static void main(String[] args) {
        /* TO DO */
        try{
            Game g = new Game();
            g.playGame();
            g.congratulate();
            System.exit(0);
        }
        catch (MalformedURLException e){ e.printStackTrace() ; System.exit(0);}
        catch (RemoteException e){ e.printStackTrace(); System.exit(0);}
        catch (NotBoundException e){ e.printStackTrace(); System.exit(0);}
    }
 
}