import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TicTacToeServer {
    private static final int PORT = 12345;
    private static final int MAX_PLAYERS = 2;

    private ServerSocket serverSocket;
    private PrintWriter[] playerWriters;
    private Scanner[] playerScanners;
    private int currentPlayer;

    public TicTacToeServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            playerWriters = new PrintWriter[MAX_PLAYERS];
            playerScanners = new Scanner[MAX_PLAYERS];
            currentPlayer = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            System.out.println("Waiting for players to connect...");
            for (int i = 0; i < MAX_PLAYERS; i++) {
                Socket socket = serverSocket.accept();
                System.out.println("Player " + (i + 1) + " connected.");

                playerWriters[i] = new PrintWriter(socket.getOutputStream(), true);
                playerScanners[i] = new Scanner(socket.getInputStream());

                Thread playerHandlerThread = new Thread(new PlayerHandler(i));
                playerHandlerThread.start();
            }
            System.out.println("Two players connected. Game starting...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcastMove(int currentPlayer, String move) {
        for (int i = 0; i < MAX_PLAYERS; i++) {
            if (i != currentPlayer) {
                playerWriters[i].println(move);
            }
        }
    }

    private class PlayerHandler implements Runnable {
        private int playerIndex;

        public PlayerHandler(int playerIndex) {
            this.playerIndex = playerIndex;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    if (playerScanners[playerIndex].hasNextLine()) {
                        String move = playerScanners[playerIndex].nextLine();
                        broadcastMove(playerIndex, move);
                    }
                }
            } finally {
                playerScanners[playerIndex].close();
				playerWriters[playerIndex].close();
            }
        }
    }

    public static void main(String[] args) {
        TicTacToeServer server = new TicTacToeServer();
        server.start();
    }
}
