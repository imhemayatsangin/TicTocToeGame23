import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;



public class TicTacToeGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private JButton[][] buttons;
    private int currentPlayer; // Player 1: 1, Player 2: 2
    private int[][] board;
    private int rounds;
    private int player1Score=0;
    private int player2Score=0;
    private int totalTie=0;
    private JLabel scoreboardLabel;
    private int row,col;
    private int currentRound;

    private ArrayList<String[]> scoreRecords;
    private DefaultTableModel tableModel;
    private JTable resultTable;
    
    // Create labels for the scoreboard
    private ArrayList<String[]> scoreboardLabels;
    private DefaultTableModel ScoreCardTable;
    private JTable resultScoreCardTable;
    


    public TicTacToeGUI() {
    	
    	  scoreRecords = new ArrayList<>();
          scoreboardLabels = new ArrayList<>();
          
          createResultTable(); // Create the result table
//          generateRandomData();
          
          
    	 // Initialize the resultTable with the DefaultTableModel
        tableModel = new DefaultTableModel(new String[0][], new String[]{"Round", "Player 1", "Player 2"});
        resultTable = new JTable(tableModel);

        // Initialize the resultScoreCardTable with the DefaultTableModel
//        ScoreCardTable = new DefaultTableModel(new String[0][], new String[]{"Player 1:(X)", "Tie", "Player 2:(O)"});
//        resultScoreCardTable = new JTable(ScoreCardTable);
//     
        
        setTitle("Tic-Tac-Toe Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(450, 750));
        setLayout(new BorderLayout());

        JPanel gamePanel = new JPanel(new GridLayout(3, 3));
        buttons = new JButton[3][3];
        board = new int[3][3];
        currentPlayer = 1;
        rounds = 0;
    
      

        // Create buttons for each cell of the game board
        for ( row = 0; row < 3; row++) {
            for ( col = 0; col < 3; col++) {
                buttons[row][col] = new JButton();
                buttons[row][col].setFont(new Font("Arial", Font.BOLD, 48));
                buttons[row][col].setPreferredSize(new Dimension(100, 100)); // Set preferred size
                buttons[row][col].setBackground(new Color(170,187,204)); // Set the button background color to170 187 204
               // buttons[row][col].setBackground(new Color(39,170,225)); // Set the button background color to Sky Blue170 187 204
             //   buttons[row][col].setBackground( Color.ORANGE);
                gamePanel.add(buttons[row][col]);

                int finalRow = row;
                int finalCol = col;
                buttons[row][col].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (board[finalRow][finalCol] == 0) {
                        	// Mark the cell with the current player's symbol
                        	board[finalRow][finalCol] = currentPlayer;
                        	String symbol = currentPlayer == 1 ? "X" : "O";
                        	
                        	buttons[finalRow][finalCol].setText(symbol);
                        	if (currentPlayer == 1) {
                        	    buttons[finalRow][finalCol].setForeground(Color.RED); // Set X color to red
                        	} else {
                        	    buttons[finalRow][finalCol].setForeground(Color.GREEN); // Set O color to green
                        	}


                        	
                        	buttons[finalRow][finalCol].setEnabled(false);
                        	
                        	
                        	
                        	buttons[finalRow][finalCol].setOpaque(true);
                        	buttons[finalRow][finalCol].setBackground(new Color(170, 187, 204).brighter());

                        	

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

        player1Score =player1Score + player1Result;
        player2Score =  player2Score +player2Result;
        totalTie = totalTie +totalTieResult;

       
        
        currentRound = (scoreRecords.size() % 10)+1;
        String[] resultRow = {Integer.toString(currentRound), Integer.toString(player1Result), Integer.toString(player2Result)};
        scoreRecords.add(resultRow);
     tableModel.setDataVector(scoreRecords.toArray(new String[0][]), new String[]{"Round", "Player 1", "Player 2"});
     
     if (!scoreboardLabels.isEmpty()) {
    	    scoreboardLabels.remove(scoreboardLabels.size() - 1);
    	}

    	for (String[] row : scoreboardLabels) {
    	    player1Score += Integer.parseInt(row[1]);
    	    totalTie += Integer.parseInt(row[2]);
    	    player2Score += Integer.parseInt(row[3]);
    	}

    	String[] resultRow1 = {Integer.toString(player1Score), Integer.toString(totalTie), Integer.toString(player2Score)};
    	scoreboardLabels.add(resultRow1); // Add the sum row
    	ScoreCardTable.setDataVector(scoreboardLabels.toArray(new String[0][]), new String[]{"Player 1:(X)", "Tie", "Player 2:(O)"});
   
     
        
        if (currentRound == 10) {
       
        	
            if (scoreboardLabels.size() > 0) {
                scoreboardLabels.remove(scoreboardLabels.size() - 1);
            }
            
            for (String[] row : scoreboardLabels) {
            	player1Score += Integer.parseInt(row[1]);
            	totalTie += Integer.parseInt(row[2]);
            	player2Score += Integer.parseInt(row[3]);
            }
            
        
          
    		            String[] resultRow2 = {Integer.toString(player1Score), Integer.toString(totalTie), Integer.toString(player2Score)};
    		         
    		            scoreboardLabels.add(resultRow2); // Add the sum row
    		            ScoreCardTable.setDataVector(scoreboardLabels.toArray(new String[0][]), new String[]{"Player 1:(X)", "Tie", "Player 2:(O)"});
    		            

            if (player1Score > player2Score) {
                JOptionPane.showMessageDialog(null, "Player 1 wins the game with " + player1Score + " rounds!");
            } else if (player1Score < player2Score) {
                JOptionPane.showMessageDialog(null, "Player 2 wins the game with " + player2Score + " rounds!");
            } else {
                JOptionPane.showMessageDialog(null, "The game is tied. Both players have the same number of rounds!");
            }

            tableModel.setRowCount(0);
         
            currentRound = 0;
         
           
            JOptionPane.showMessageDialog(null, "Game restarted! Starting a new game.");
            
            // Create a timer that triggers after 2 seconds
            Timer timer = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	 resetGameAndScoreboard();
                ScoreCardTable.setRowCount(0);
            	scoreboardLabels.clear();
            	  player1Score =0;
                  player2Score =  0;
                  totalTie = 0;
            	
                }
            });

            // Start the timer
            timer.setRepeats(false); // Set to false to only run once
            timer.start();
            
            
        }
     
     
       
        
  

		            
//		            System.out.println( scoreboardLabels.add(resultRow1));
		            
		         
		            
		      
					
    }
    

    private void resetGame() {
    
        // Reset the game board and enable buttons
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                board[row][col] = 0;
                buttons[row][col].setText("");
                buttons[row][col].setEnabled(true);
                
                buttons[row][col].setBackground(new Color(170,187,204)); // Set the button background color to170 187 204
            }
        }

        currentPlayer = 1;
        rounds++;


    }
   
    
    // Method to add a row to the table
    public void addRowToScoreTable(String player1TotalScore, String totalTie, String playerTotalScore) {
    	 
    	
    	  String[] resultRow1 = {player1TotalScore, totalTie, playerTotalScore};
         scoreboardLabels.add(resultRow1);
         ScoreCardTable.setDataVector(scoreboardLabels.toArray(new String[0][]), new String[]{"Player 1:(X)", "Tie", "Player 2:(O)"});
        
    }

    // Method to add a row to the table
    public void addRowToTable(String round, String player1Score, String player2Score) {
    	 
      String[] resultRow = {round, player1Score, player2Score};
      scoreRecords.add(resultRow);
      tableModel.setDataVector(scoreRecords.toArray(new String[0][]), new String[]{"Round", "Player 1", "Player 2"});
        
    }
    
    //end of game resumed
    
    
    private void resetGameAndScoreboard() {
        resetGame();
        scoreRecords.clear();
        scoreboardLabels.clear();
        tableModel = new DefaultTableModel(new String[0][], new String[]{"Round", "Player 1", "Player 2"});
        resultTable.setModel(tableModel);
        
       
        ScoreCardTable = new DefaultTableModel(new String[0][], new String[]{"Player 1:(X)", "Tie", "Player 2:(O)"});
        resultScoreCardTable.setModel(ScoreCardTable); // Add this line to set the new model to the resultScoreCardTable
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
            TicTacToeGUI gui = new TicTacToeGUI();
            gui.setVisible(true);
        });
    }
}
