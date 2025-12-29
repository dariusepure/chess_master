import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameOverFrame extends JFrame {
    private Main app;
    private Game game;
    private ChessGUI gui;

    public GameOverFrame(Main app, Game game, String result, int points, ChessGUI gui) {
        this.app = app;
        this.game = game;
        this.gui = gui;
        setTitle("Game Over");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents(result, points);
    }

    private void initComponents(String result, int points) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel resultLabel = new JLabel(result, SwingConstants.CENTER);
        resultLabel.setFont(new Font("Serif", Font.BOLD, 24));
        if (result.contains("Victory") || result.contains("Win")) {
            resultLabel.setForeground(Color.GREEN);
        } else if (result.contains("Defeat") || result.contains("Resign")) {
            resultLabel.setForeground(Color.RED);
        } else {
            resultLabel.setForeground(Color.BLUE);
        }
        mainPanel.add(resultLabel, BorderLayout.NORTH);
        JPanel detailsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        Player player1 = game.getPlayer1();
        Player player2 = game.getPlayer2();

        detailsPanel.add(new JLabel("Players: " + player1.getName() + " vs " + player2.getName()));
        detailsPanel.add(new JLabel("Moves played: " + game.getHistory().size()));
        detailsPanel.add(new JLabel("Points earned/lost: " + (points >= 0 ? "+" : "") + points));
        detailsPanel.add(new JLabel("New total points: " + app.getCurrentUser().getPoints()));
        mainPanel.add(detailsPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> {
            dispose();
            gui.showNewGameScreen();
        });
        JButton mainMenuButton = new JButton("Main Menu");
        mainMenuButton.addActionListener(e -> {
            dispose();
            gui.showMainMenu();
        });
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> gui.exit());
        buttonPanel.add(newGameButton);
        buttonPanel.add(mainMenuButton);
        buttonPanel.add(exitButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }
}