import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TicTacToeServer {
    private static final int PORT = 1234;

    private char[][] board;
    private PrintWriter[] clientWriters;
    private int currentPlayer;
    private static final int MAX_PLAYERS = 2;
    private ServerSocket serverSocket;

    public TicTacToeServer() {
        board = new char[3][3];
        clientWriters = new PrintWriter[MAX_PLAYERS];
        currentPlayer = 0;

        for (int i = 0; i < MAX_PLAYERS; i++) {
            clientWriters[i] = null;
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
    }

    public static void main(String[] args) {
        TicTacToeServer server = new TicTacToeServer();
        server.run();
    }

    private void run() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Waiting for clients to connect...");

            while (true) {
                Socket socket = serverSocket.accept();
                int player = getPlayerIndex();
                System.out.println("Client " + player + " connected.");

                clientWriters[player] = new PrintWriter(socket.getOutputStream(), true);

                Thread clientThread = new Thread(new ClientHandler(socket, player));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getPlayerIndex() {
        return currentPlayer++;
    }

    private void broadcastMessage(String message) {
        for (int i = 0; i < MAX_PLAYERS; i++) {
            if (clientWriters[i] != null) {
                clientWriters[i].println(message);
            }
        }
    }

    private void shutdown() {
        try {
            for (int i = 0; i < MAX_PLAYERS; i++) {
                if (clientWriters[i] != null) {
                    clientWriters[i].println("QUIT");
                    clientWriters[i].close();
                }
            }

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private Scanner clientScanner;
        private PrintWriter clientWriter;
        private int player;

        public ClientHandler(Socket socket, int player) {
            this.socket = socket;
            this.player = player;
        }

        @Override
        public void run() {
            try {
                clientScanner = new Scanner(socket.getInputStream());
                clientWriter = clientWriters[player];

                broadcastMessage("Player " + player + " joined the game.");

                while (true) {
                    if (clientScanner.hasNextLine()) {
                        String message = clientScanner.nextLine();
                        if (message.equals("QUIT")) {
                            clientWriter.println("QUIT");
                            break;
                        } else {
                            processMove(message);
                        }
                    }
                }

                socket.close();
                clientScanner.close();
                clientWriter.close();
                clientWriters[player] = null;
                broadcastMessage("Player " + player + " left the game.");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void processMove(String move) {
            int row = Character.getNumericValue(move.charAt(0));
            int col = Character.getNumericValue(move.charAt(1));

            if (board[row][col] == ' ') {
                board[row][col] = (player == 0) ? 'X' : 'O';

                broadcastMessage("MOVE " + player + " " + row + " " + col);

                if (checkWin()) {
                    broadcastMessage("WIN " + player);
                    broadcastMessage("QUIT");
                    shutdown();
                } else if (checkDraw()) {
                    broadcastMessage("DRAW");
                    broadcastMessage("QUIT");
                    shutdown();
                }
            }
        }

        private boolean checkWin() {
            // Check rows
            for (int i = 0; i < 3; i++) {
                if (board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][0] == board[i][2]) {
                    return true;
                }
            }

            // Check columns
            for (int j = 0; j < 3; j++) {
                if (board[0][j] != ' ' && board[0][j] == board[1][j] && board[0][j] == board[2][j]) {
                    return true;
                }
            }

            // Check diagonals
            if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[0][0] == board[2][2]) {
                return true;
            }

            if (board[0][2] != ' ' && board[0][2] == board[1][1] && board[0][2] == board[2][0]) {
                return true;
            }

            return false;
        }

        private boolean checkDraw() {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == ' ') {
                        return false;
                    }
                }
            }
            return true;
        }
    }
}
