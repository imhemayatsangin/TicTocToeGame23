import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TicTacToeGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private JButton[][] buttons;
    private int currentPlayer; // Player 1: 1, Player 2: 2
    private int[][] board;
    private int rounds;
    private int player1Score;
    private int player2Score;
    private int totalTie;
    private JLabel scoreboardLabel;
  
    private JLabel player1ScoreLabel;
    private JLabel player2ScoreLabel;
    private JLabel roundsScoreLabel;
    private ArrayList<String[]> scoreRecords;
    private DefaultTableModel tableModel;
    private JTable resultTable;
    
    // Create labels for the scoreboard
    private JLabel player1Label = new JLabel("Player 1(X)");
    private JLabel roundsLabel = new JLabel("Tie");
    private JLabel player2Label = new JLabel("Player 2(O)");

    public TicTacToeGUI() {
        setTitle("Tic-Tac-Toe Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(400, 500));
        setLayout(new BorderLayout());

        JPanel gamePanel = new JPanel(new GridLayout(3, 3));
        buttons = new JButton[3][3];
        board = new int[3][3];
        currentPlayer = 1;
        rounds = 0;
        player1Score = 0;
        player2Score = 0;
        totalTie = 0;
        scoreRecords = new ArrayList<>();

        // Create buttons for each cell of the game board
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col] = new JButton();
                buttons[row][col].setFont(new Font("Arial", Font.BOLD, 48));
                buttons[row][col].setBackground(Color.ORANGE); // Set the button background color to orange
                gamePanel.add(buttons[row][col]);

                int finalRow = row;
                int finalCol = col;
                buttons[row][col].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (board[finalRow][finalCol] == 0) {
                            // Mark the cell with the current player's symbol
                            board[finalRow][finalCol] = currentPlayer;
                            buttons[finalRow][finalCol].setText(currentPlayer == 1 ? "X" : "O");
                            buttons[finalRow][finalCol].setEnabled(false);

                            // Check if the current player wins
                            if (checkWin(currentPlayer)) {
                                JOptionPane.showMessageDialog(null, "Player " + currentPlayer + " wins!");
                                updateScoreboard(currentPlayer);
                                resetGame();
                            } else if (checkTie()) {
                                JOptionPane.showMessageDialog(null, "It's a tie!");
                                updateScoreboard(0);
                                resetGame();
                            } else {
                                currentPlayer = currentPlayer == 1 ? 2 : 1;
                                rounds++;
                            }
                        }
                    }
                });
            }
        }
        gamePanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add spacing around the game panel
        gamePanel.setBackground(Color.WHITE); // Set the border color between the buttons to red

        scoreboardLabel = new JLabel("Score Board");
        scoreboardLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        scoreboardLabel.setBorder(new EmptyBorder(0, 0, 0, 10)); // Add spacing on the right side of the status label

     
        JPanel scoreboardPanel = new JPanel(new GridLayout(2, 3));
        scoreboardPanel.setPreferredSize(new Dimension(400, 100));
        scoreboardPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add spacing around the scoreboard panel

 

        player1ScoreLabel = new JLabel(Integer.toString(player1Score));
        roundsScoreLabel = new JLabel(Integer.toString(rounds));
        player2ScoreLabel = new JLabel(Integer.toString(player2Score));

        // Add the labels to the scoreboard panel
        scoreboardPanel.add(player1Label);
        scoreboardPanel.add(roundsLabel);
        scoreboardPanel.add(player2Label);
        scoreboardPanel.add(player1ScoreLabel);
        scoreboardPanel.add(roundsScoreLabel);
        scoreboardPanel.add(player2ScoreLabel);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(scoreboardPanel, BorderLayout.CENTER);
        bottomPanel.add(scoreboardLabel, BorderLayout.NORTH);
//        bottomPanel.add(statusTie, BorderLayout.EAST);
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Add spacing around the bottom panel

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(gamePanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        createResultTable(); // Create the result table

        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        add(mainPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        
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
            System.exit(0);
        }
    }

    // Method to save game state to a file
    private void saveGameStateToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("src/score.txt"));
//
//            roundsScoreLabel.setText(Integer.toString(totalTie));
//            player1ScoreLabel.setText(Integer.toString(player1Score));
//            player2ScoreLabel.setText(Integer.toString(player2Score));
//            
            
            
            
            // Write scoreboard data
            writer.write("Player 1 Wins: " + player1ScoreLabel.getText());
            writer.newLine();
            writer.write("Player 2 Wins: " + player2ScoreLabel.getText());
            writer.newLine();
            writer.write("Total Ties: " + roundsScoreLabel.getText());
            writer.newLine();
            writer.newLine();

            // Write table data
            writer.write("Round\tPlayer 1\tPlayer 2");
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

    private boolean checkWin(int player) {
        // Check rows
        for (int row = 0; row < 3; row++) {
            if (board[row][0] == player && board[row][1] == player && board[row][2] == player) {
                return true;
            }
        }

        // Check columns
        for (int col = 0; col < 3; col++) {
            if (board[0][col] == player && board[1][col] == player && board[2][col] == player) {
                return true;
            }
        }

        // Check diagonals
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true;
        }

        if (board[2][0] == player && board[1][1] == player && board[0][2] == player) {
            return true;
        }

        return false;
    }

    private boolean checkTie() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] == 0) {
                    return false; // Found an empty cell, game is not tied
                }
            }
        }
        return true; // All cells are filled, game is tied
    }

    private void updateScoreboard(int winner) {
        int player1Result = 0;
        int player2Result = 0;
        int totalTieResult = 0;

        if (winner == 1) {
            player1Result = 1;
        } else if (winner == 2) {
            player2Result = 1;
        } else if (checkTie()) {
            player1Result = 0;
            player2Result = 0;
            totalTieResult = 1;
        }

        player1Score += player1Result;
        player2Score += player2Result;
        totalTie += totalTieResult;

        int currentRound = (scoreRecords.size() % 10) + 1;
        String[] resultRow = {Integer.toString(currentRound), Integer.toString(player1Result), Integer.toString(player2Result)};
        scoreRecords.add(resultRow);

        
        
        
        tableModel.setDataVector(scoreRecords.toArray(new String[0][]), new String[]{"Round", "Player 1", "Player 2"});
        
					if(currentRound==10) {
					        	
						
						roundsScoreLabel.setText(Integer.toString(totalTie));
			            player1ScoreLabel.setText(Integer.toString(player1Score));
			            player2ScoreLabel.setText(Integer.toString(player2Score));
			            
			            if(player1Score>player2Score) {
			            	 JOptionPane.showMessageDialog(null, "Player 1 Win the Game! with( " + player1Score +" )Rounds!" );
			            	tableModel.setRowCount(0);
			            	currentRound = 0;
			            	 resetGameAndScoreboard();
			                 JOptionPane.showMessageDialog(null, "Game Restarted! Starting a new game.");
			            }
			            else if(player1Score<player2Score) {
			            	 JOptionPane.showMessageDialog(null, "Player 2 Win the Game! with( " + player2Score +" )Rounds!" );
				            	tableModel.setRowCount(0);
				            	currentRound = 0;
				            	 resetGameAndScoreboard();
				                 JOptionPane.showMessageDialog(null, "Game Restarted! Starting a new game.");
			            }
			            else {
			           	 JOptionPane.showMessageDialog(null, "Game is tied and Both the players are equal!" );
			            	tableModel.setRowCount(0);
			            	currentRound = 0;
			            	 resetGameAndScoreboard();
			                 JOptionPane.showMessageDialog(null, "Game Restarted! Starting a new game.");
			            }
			            
			            
			            
					        }
    }

    private void resetGame() {
        // Reset the game board and enable buttons
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                board[row][col] = 0;
                buttons[row][col].setText("");
                buttons[row][col].setEnabled(true);
            }
        }

        currentPlayer = 1;
        rounds++;


    }

    
    private void resetGameAndScoreboard() {
        resetGame();
        scoreRecords.clear();
        tableModel = new DefaultTableModel(new String[0][], new String[]{"Round", "Player 1", "Player 2"});
        resultTable.setModel(tableModel);
    }


    private void createResultTable() {
        tableModel = new DefaultTableModel(new String[0][], new String[]{"Round", "Player 1", "Player 2"});
        resultTable = new JTable(tableModel);
        resultTable.setEnabled(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TicTacToeGUI gui = new TicTacToeGUI();
            gui.setVisible(true);
        });
    }
}
