import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
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

    
    private ArrayList<String[]> scoreRecords;
    private DefaultTableModel tableModel;
    private JTable resultTable;
    
    // Create labels for the scoreboard
    private ArrayList<String[]> scoreboardLabels;
    private DefaultTableModel ScoreCardTable;
    private JTable resultScoreCardTable;
    private JLabel scoreboardLabel;
    private String playerName;
    public TicTacToeClient(String playerName) {
        this.playerName = playerName;
        setTitle("TicTacToeGame-" + playerName);
        
        
   	  scoreRecords = new ArrayList<>();
      scoreboardLabels = new ArrayList<>();
      
      createResultTable(); // Create the result table
//      generateRandomData();
      
      
	 // Initialize the resultTable with the DefaultTableModel
    tableModel = new DefaultTableModel(new String[0][], new String[]{"Round", "Player 1", "Player 2"});
    resultTable = new JTable(tableModel);

    setPreferredSize(new Dimension(450, 750));
    setLayout(new BorderLayout());

        JPanel gamePanel = new JPanel(new GridLayout(3, 3));
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
                
                gamePanel.add(button);
//                add(button);
                buttons[row][col] = button;
            }
        }

        gamePanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add spacing around the game panel
        gamePanel.setBackground(Color.WHITE); // Set the border color between the buttons to red

        scoreboardLabel = new JLabel("Score Board");
        scoreboardLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        scoreboardLabel.setBorder(new EmptyBorder(0, 0, 0, 10)); // Add spacing on the right side of the status label
      
      
        JPanel bottomPanel = new JPanel(new BorderLayout());
      
        bottomPanel.add(scoreboardLabel, BorderLayout.NORTH);
     
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add spacing around the bottom panel

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(gamePanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

 
        JScrollPane scrollPane1 = new JScrollPane(resultScoreCardTable);
        scrollPane1.setPreferredSize(new Dimension(200, 100));
        scrollPane1.setBorder(new EmptyBorder(10, 10, 10, 10));

       add(scrollPane1, BorderLayout.CENTER);
       
 
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        add(mainPanel, BorderLayout.NORTH);

        add(scrollPane, BorderLayout.SOUTH);
        
        
        
        setIconImage(new ImageIcon("src/Tic-Tac-Toe-Game.png").getImage());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        connectToServer();
        // Add window listener to handle the closing event
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                confirmAndSaveGameState();
            }
        });
        
        
    }
    
    // Method to show confirmation dialog and save game state
    private void confirmAndSaveGameState() {
        int option = JOptionPane.showConfirmDialog(null, "Do you want to save your game state?", "Save Game State",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            saveGameStateToFile();
        } else {
        	 clearScoreFile();
            System.exit(0);
        }
    }

 // Method to clear the contents of the score file
    private void clearScoreFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("src/score.txt", false));
            writer.write("");  // Write an empty string to clear the contents of the file
            writer.close();
            System.out.println("Score file cleared.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    // Method to save game state to a file
    private void saveGameStateToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("src/score.txt"));
           
            
            
            
            // Write scoreboard data
            writer.write("Player1\tTie\tPlayer2");
            writer.newLine();
            for (String[] scoreboardLabel : scoreboardLabels) {
                for (String field1 : scoreboardLabel) {
                    writer.write(field1 + "\t");
                }
                writer.newLine();
            }
//            writer.newLine();

            // Write table data
           writer.write("Round\tPlayer1\tPlayer2");
            writer.newLine();
            for (String[] record : scoreRecords) {
                for (String field : record) {
                    writer.write(field + "\t");
                }
                writer.newLine();
            }

            writer.close();

            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    private void createResultTable() {
        tableModel = new DefaultTableModel(new String[0][], new String[]{"Round", "Player 1", "Player 2"});
        resultTable = new JTable(tableModel);
        resultTable.setEnabled(false);
        
        
       
        ScoreCardTable = new DefaultTableModel(new String[0][], new String[]{"Player 1:(X)", "Tie", "Player 2:(O)"});
        resultScoreCardTable = new JTable(ScoreCardTable);
        resultScoreCardTable.setEnabled(false);
        
       
        
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        	TicTacToeClient client1 = new TicTacToeClient("Player 1(X)");
            client1.setLocationRelativeTo(null);
            client1.playerSymbol = 'X';
            client1.opponentSymbol = 'O';

            TicTacToeClient client2 = new TicTacToeClient("Player 2(O)");
            client2.setLocationRelativeTo(null);
            client2.playerSymbol = 'O';
            client2.opponentSymbol = 'X';
        });
    }
}
