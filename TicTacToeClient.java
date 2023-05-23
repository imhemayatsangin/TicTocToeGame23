import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TicTacToeClient extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3456485401222133833L;
	private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    private char[][] board;
    private JButton[][] buttons;
    private PrintWriter serverWriter;
    private Scanner serverScanner;
    private char playerSymbol;
    private char opponentSymbol;

    public TicTacToeClient() {
        setTitle("Tic Tac Toe");
        setPreferredSize(new Dimension(450, 750));
        setLayout(new GridLayout(3, 3));

        board = new char[3][3];
        buttons = new JButton[3][3];

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                JButton button = new JButton();
                button.setFont(new Font("Arial", Font.BOLD, 48));
//                buttons[row][col].setPreferredSize(new Dimension(100, 100)); // Set preferred size
              //  buttons[row][col].setBackground(new Color(170,187,204)); // Set the button background color to170 187 204
                button.addActionListener(new ButtonClickListener(row, col));
//                button.setBackground(Color.WHITE); // Set the background color
                button.setPreferredSize(new Dimension(100, 100)); // Set the preferred size
                add(button);
                buttons[row][col] = button;
            }
        }

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        connectToServer();
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            serverWriter = new PrintWriter(socket.getOutputStream(), true);
            serverScanner = new Scanner(socket.getInputStream());

            Thread serverReaderThread = new Thread(new ServerReader());
            serverReaderThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMove(int row, int col) {
        serverWriter.println(row + "," + col);
    }

    private void handleMove(String move) {
        String[] coordinates = move.split(",");
        int row = Integer.parseInt(coordinates[0]);
        int col = Integer.parseInt(coordinates[1]);

        char symbol = getOpponentSymbol();
        board[row][col] = symbol;
        buttons[row][col].setText(Character.toString(symbol));
        buttons[row][col].setEnabled(false);

        if (checkWin(symbol)) {
            JOptionPane.showMessageDialog(TicTacToeClient.this, "Player " + symbol + " wins!");
            resetBoard();
        } else if (isBoardFull()) {
            JOptionPane.showMessageDialog(TicTacToeClient.this, "It's a draw!");
            resetBoard();
        }
    }

    private boolean checkWin(char symbol) {
        // Check rows
        for (int row = 0; row < 3; row++) {
            if (board[row][0] == symbol && board[row][1] == symbol && board[row][2] == symbol) {
                return true;
            }
        }

        // Check columns
        for (int col = 0; col < 3; col++) {
            if (board[0][col] == symbol && board[1][col] == symbol && board[2][col] == symbol) {
                return true;
            }
        }

        // Check diagonals
        if (board[0][0] == symbol && board[1][1] == symbol && board[2][2] == symbol) {
            return true;
        }
        if (board[0][2] == symbol && board[1][1] == symbol && board[2][0] == symbol) {
            return true;
        }

        return false;
    }

    private boolean isBoardFull() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] == '\u0000') {
                    return false;
                }
            }
        }
        return true;
    }

    private void resetBoard() {
        board = new char[3][3];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col].setText("");
                buttons[row][col].setEnabled(true);
            }
        }
    }

    private char getPlayerSymbol() {
        return playerSymbol;
    }

    private char getOpponentSymbol() {
        return opponentSymbol;
    }

    private class ButtonClickListener implements ActionListener {
        private int row;
        private int col;

        ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (playerSymbol == '\u0000') {
                JOptionPane.showMessageDialog(TicTacToeClient.this, "Waiting for the opponent to join...");
                return;
            }

            // Disable buttons for the opponent player
            disableOpponentButtons();

            char symbol = getPlayerSymbol();
            board[row][col] = symbol;
            buttons[row][col].setText(Character.toString(symbol));
            buttons[row][col].setEnabled(false);

            if (checkWin(symbol)) {
                JOptionPane.showMessageDialog(TicTacToeClient.this, "Player " + symbol + " wins!");
                resetBoard();
            } else if (isBoardFull()) {
                JOptionPane.showMessageDialog(TicTacToeClient.this, "It's a draw!");
                resetBoard();
            }

            sendMove(row, col);
        }

        private void disableOpponentButtons() {
            char opponentSymbol = getOpponentSymbol();
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    if (board[row][col] == '\u0000') {
                        buttons[row][col].setEnabled(opponentSymbol != '\u0000');
                    }
                }
            }
        }
    }

    private class ServerReader implements Runnable {
        @Override
        public void run() {
            try {
                while (serverScanner.hasNextLine()) {
                    String move = serverScanner.nextLine();
                    handleMove(move);
                }
            } finally {
                serverScanner.close();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TicTacToeClient client1 = new TicTacToeClient();
            client1.setLocationRelativeTo(null);
            client1.playerSymbol = 'X';
            client1.opponentSymbol = 'O';

            TicTacToeClient client2 = new TicTacToeClient();
            client2.setLocationRelativeTo(null);
            client2.playerSymbol = 'O';
            client2.opponentSymbol = 'X';
        });
    }
}
