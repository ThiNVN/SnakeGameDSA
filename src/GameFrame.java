/* Nguyen Van Ngoc Thi & ITCSIU22251
 Purpose: I implement this project to understand and apply the knowledge from IU-DSA course.
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class GameFrame extends JFrame {
    private Image img;
    private JButton startButton;
    private JButton rulesButton;
    private JButton quitButton;

    public GameFrame() {
        setTitle("Snake Game Menu");
        setSize(600, 600); // Adjusted size for demonstration
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame on screen

        img = Toolkit.getDefaultToolkit().getImage("src/Resources/SnakeGame.png");

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, this);
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Use BoxLayout

        Font titleFont = new Font("Pixeboy", Font.BOLD, 100);
        JLabel titleLabel = new JLabel("Snake Game");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titleLabel.setBorder(BorderFactory.createEmptyBorder(100, 0, 0, 0));

        panel.add(titleLabel);
        panel.add(Box.createVerticalGlue()); // Add glue to push buttons to the top

        startButton = new JButton("Start");
        rulesButton = new JButton("Rules");
        quitButton = new JButton("Quit");

        startButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align buttons
        rulesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        rulesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRules();
            }
        });

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        panel.add(Box.createVerticalGlue()); // Add glue to push buttons to the top
        panel.add(startButton);
        panel.add(Box.createVerticalStrut(50)); // Add spacing between buttons
        panel.add(rulesButton);
        panel.add(Box.createVerticalStrut(50));
        panel.add(quitButton);
        panel.add(Box.createVerticalGlue()); // Add glue to push buttons to the bottom

        add(panel);
        setVisible(true);
    }

    private void startGame() {
        JFrame gameFrame = new JFrame("Snake Game");
        GamePanel gamePanel = new GamePanel();
        gameFrame.add(gamePanel);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.pack();
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);
        dispose(); // Close the menu frame after starting the game
    }

    private void showRules() {
        JOptionPane.showMessageDialog(this,
                "Rules of the Snake Game:\n" +
                        "- Use arrow keys to control the snake.\n" +
                        "- Eat apples to score points and blue buff to shorten yourself.\n" +
                        "- Avoid collisions with walls and yourself.\n" +
                        "- Reach level 2 by eating 10 apples in level 1 and win by eating 10 apples in level 2.",
                "Rules",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
