import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import java.io.File;

public class TicTacToeClient extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5479639073618861665L;
	private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 1234;

    private char[][] board;
    private PrintWriter serverWriter;
    private JButton[][] buttons;
    
    private ArrayList<String[]> scoreRecords;
    private DefaultTableModel tableModel;
    private JTable resultTable;
    private JLabel scoreboardLabel;
    // Create labels for the scoreboard
    private ArrayList<String[]> scoreboardLabels;
    private DefaultTableModel ScoreCardTable;
    private JTable resultScoreCardTable;

    public TicTacToeClient(int player) {
        super("Tic Tac Toe - Player " + player);
        board = new char[3][3];
//        board = new int[3][3];
        scoreRecords = new ArrayList<>();
        scoreboardLabels = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }

        
        createResultTable(); // Create the result table
//      generateRandomData();
      
      
	 // Initialize the resultTable with the DefaultTableModel
    tableModel = new DefaultTableModel(new String[0][], new String[]{"Round", "Player 1", "Player 2"});
    resultTable = new JTable(tableModel);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setPreferredSize(new Dimension(450, 750));
    setLayout(new BorderLayout());

   
  
  
 
        buttons = new JButton[3][3];
        
        
        JPanel gamePanel = new JPanel(new GridLayout(3, 3));

        ButtonHandler buttonHandler = new ButtonHandler();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton();
                
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 48));
                buttons[i][j].setPreferredSize(new Dimension(100, 100)); // Set preferred size
                buttons[i][j].setBackground(new Color(170,187,204)); // Set the button background color to170 187 204

                buttons[i][j].addActionListener(buttonHandler);
                buttons[i][j].putClientProperty("row", i);
                buttons[i][j].putClientProperty("col", j);
                gamePanel.add(buttons[i][j]);
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
        setResizable(false);
        setVisible(true);

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TicTacToeClient player1 = new TicTacToeClient(1);
            TicTacToeClient player2 = new TicTacToeClient(2);
            player1.run();
            player2.run();
        });
    }

    private void run() {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            serverWriter = new PrintWriter(socket.getOutputStream(), true);

            Thread serverThread = new Thread(new ServerHandler(socket));
            serverThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateBoard(int row, int col, char symbol) {
        buttons[row][col].setText(Character.toString(symbol));
        buttons[row][col].setEnabled(false);
    }

    private void handleGameEnd() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setEnabled(false);
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

    private class ButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            int row = (int) button.getClientProperty("row");
            int col = (int) button.getClientProperty("col");

            button.setEnabled(false);
            serverWriter.println(row + "" + col);
        }
    }

    private class ServerHandler implements Runnable {
        private Socket socket;
        private Scanner serverScanner;

        public ServerHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                serverScanner = new Scanner(socket.getInputStream());

                while (serverScanner.hasNextLine()) {
                    String message = serverScanner.nextLine();
                    if (message.equals("QUIT")) {
                        break;
                    } else if (message.startsWith("MOVE")) {
                        processMove(message);
                    } else if (message.startsWith("WIN")) {
                        int player = Character.getNumericValue(message.charAt(4));
                        handleGameEnd();
                        if(player==0) {
                        	 JOptionPane.showMessageDialog(null, "Player 1 (X) win the game!");
                        }
                        else {
                        	
                        	 JOptionPane.showMessageDialog(null, "Player 2 (O) win the game!");
                        }
                        
                        resetGame();
                        
                       
                      
                        break;
                    } else if (message.equals("DRAW")) {
                    	 handleGameEnd();
                        System.out.println("It's a draw!");
                       
                        break;
                    }
                }

                socket.close();
                serverScanner.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Reset the game board
        private void resetGame() {
            // Reset the game board
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    board[i][j] = ' ';
                    buttons[i][j].setText("");
                    buttons[i][j].setEnabled(true);
                    
                    buttons[i][j].setBackground(new Color(170,187,204)); // Set the button background color to170 187 204
                }
            }
            
            // Clear score records and update result table
//            scoreRecords.clear();
//            tableModel.setRowCount(0);
            
            // Re-enable buttons and clear the result table
//            resultTable.setEnabled(true);
            
            // Handle any other reset logic you may have
        }


        
        
        private void processMove(String move) {
            String[] parts = move.split(" ");
            int player = Integer.parseInt(parts[1]);
            int row = Integer.parseInt(parts[2]);
            int col = Integer.parseInt(parts[3]);

            SwingUtilities.invokeLater(() -> updateBoard(row, col, (player == 0) ? 'X' : 'O'));
        }
    }
}




