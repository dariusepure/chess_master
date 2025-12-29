import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameOverFrame extends JFrame {
    private Main app;
    private Game game;
    private ChessGUI gui;

    // MODIFICA: Adaugă parametrii pentru toate punctele
    public GameOverFrame(Main app, Game game, String result, int bonusPoints, int capturePoints, ChessGUI gui) {
        this.app = app;
        this.game = game;
        this.gui = gui;
        setTitle("Game Over");
        setSize(550, 450); // Mărește puțin dimensiunea
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Calculează totalul AICI
        int totalPoints = capturePoints + bonusPoints;

        initComponents(result, bonusPoints, capturePoints, totalPoints);
    }

    // MODIFICA: Primește toate punctele
    private void initComponents(String result, int bonusPoints, int capturePoints, int totalPoints) {
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

        // PANOU CU DETALII COMPLETE DESPRE PUNCTE
        JPanel detailsPanel = new JPanel(new GridLayout(6, 1, 10, 10));

        Player player1 = game.getPlayer1();
        Player player2 = game.getPlayer2();

        detailsPanel.add(new JLabel("Players: " + player1.getName() + " vs " + player2.getName()));
        detailsPanel.add(new JLabel("Moves played: " + game.getHistory().size()));

        // MODIFICA: Afișează toate punctele
        detailsPanel.add(new JLabel("Points from captures: " + (capturePoints >= 0 ? "+" : "") + capturePoints));
        detailsPanel.add(new JLabel("Game bonus: " + (bonusPoints >= 0 ? "+" : "") + bonusPoints));

        // Separator vizual
        JSeparator separator = new JSeparator();
        separator.setForeground(Color.GRAY);
        detailsPanel.add(separator);

        // MODIFICA: Afișează TOTALUL
        JLabel totalLabel = new JLabel("TOTAL POINTS: " + (totalPoints >= 0 ? "+" : "") + totalPoints);
        totalLabel.setFont(new Font("Serif", Font.BOLD, 16));
        totalLabel.setForeground(totalPoints >= 0 ? new Color(0, 150, 0) : Color.RED);
        detailsPanel.add(totalLabel);

        // Punctajul total al utilizatorului
        if (app.getCurrentUser() != null) {
            User user = app.getCurrentUser();
            int userTotalPoints = user.getPoints();
            detailsPanel.add(new JLabel("Your total points: " + userTotalPoints));
        }

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