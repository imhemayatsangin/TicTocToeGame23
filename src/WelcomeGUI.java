import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class WelcomeGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    public WelcomeGUI() {
        setTitle("Tic Tac Toe Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(400, 500));
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE); // Set the background color

        JLabel welcomeLabel = new JLabel("Welcome!");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 24));

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(Color.WHITE); // Set the background color

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 50, 10, 50); // Add spacing

        JButton newGameButton = new JButton("New Game");
        newGameButton.setFont(new Font("Arial", Font.BOLD, 18));
        newGameButton.setBackground(new Color(30, 144, 255)); // Set custom background color
        newGameButton.setForeground(Color.WHITE); // Set the text color
        buttonPanel.add(newGameButton, gbc);

        JButton resumeGameButton = new JButton("Resume Game");
        resumeGameButton.setFont(new Font("Arial", Font.BOLD, 18));
        resumeGameButton.setBackground(new Color(50, 205, 50)); // Set custom background color
        resumeGameButton.setForeground(Color.WHITE); // Set the text color
        buttonPanel.add(resumeGameButton, gbc);

        JPanel emptyPanel = new JPanel(); // Empty panel for equal spacing from the bottom
        emptyPanel.setBackground(Color.WHITE); // Set the background color

        JPanel mainPanel = new JPanel(new GridBagLayout()); // Main panel to hold welcomeLabel and buttonPanel
        mainPanel.setBackground(Color.WHITE); // Set the background color
        
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.gridx = 0;
        mainGbc.gridy = 0;
        mainGbc.insets = new Insets(50, 0, 10, 0); // Add spacing

        mainPanel.add(welcomeLabel, mainGbc);

        mainGbc.gridy = 1;
        mainPanel.add(buttonPanel, mainGbc);

        mainGbc.gridy = 2;
        mainGbc.insets = new Insets(10, 0, 50, 0); // Add spacing
        mainPanel.add(emptyPanel, mainGbc); // Add empty panel for equal spacing from the bottom

        add(mainPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);

        newGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                TicTacToeGUI gameGUI = new TicTacToeGUI();
                gameGUI.setVisible(true);
            }
        });

        resumeGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                readAndResumeGameState();
            }
        });

        setVisible(true);
    }

    private void readAndResumeGameState() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/score.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Replace with code to display the data in the GUI
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
