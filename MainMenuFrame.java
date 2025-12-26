import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MainMenuFrame extends JFrame {
    private Main app;
    private ChessGUI gui;

    public MainMenuFrame(Main app, ChessGUI gui) {
        this.app = app;
        this.gui = gui;

        setTitle("Chess - Main Menu");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header cu informații utilizator
        User currentUser = app.getCurrentUser();
        JLabel userLabel = new JLabel("Welcome, " + currentUser.getEmail() +
                " | Points: " + currentUser.getPoints(), SwingConstants.CENTER);
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        mainPanel.add(userLabel, BorderLayout.NORTH);

        // Panel pentru butoane principale
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));

        JButton newGameButton = new JButton("♔ New Game");
        newGameButton.setFont(new Font("Serif", Font.BOLD, 18));
        newGameButton.addActionListener(e -> gui.showNewGameScreen());

        JButton continueButton = new JButton("↻ Continue Game");
        continueButton.setFont(new Font("Serif", Font.BOLD, 18));
        continueButton.addActionListener(e -> showContinueGameDialog());

        JButton statsButton = new JButton("📊 Account Stats");
        statsButton.setFont(new Font("Serif", Font.BOLD, 18));
        statsButton.addActionListener(e -> showStatsDialog());

        JButton logoutButton = new JButton("🚪 Logout");
        logoutButton.setFont(new Font("Serif", Font.BOLD, 18));
        logoutButton.addActionListener(e -> logout());

        buttonPanel.add(newGameButton);
        buttonPanel.add(continueButton);
        buttonPanel.add(statsButton);
        buttonPanel.add(logoutButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Footer
        JLabel activeGamesLabel = new JLabel("Active games: " +
                currentUser.getActiveGames().size(), SwingConstants.CENTER);
        mainPanel.add(activeGamesLabel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void showContinueGameDialog() {
        User currentUser = app.getCurrentUser();
        List<Game> activeGames = currentUser.getActiveGames();

        if (activeGames.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No active games!");
            return;
        }

        String[] gameOptions = new String[activeGames.size()];
        for (int i = 0; i < activeGames.size(); i++) {
            Game game = activeGames.get(i);
            gameOptions[i] = "Game #" + game.getId() + " - " +
                    game.getPlayer1().getName() + " vs " +
                    game.getPlayer2().getName() + " (" +
                    game.getCurrentPlayer().getName() + "'s turn)";
        }

        String selected = (String) JOptionPane.showInputDialog(this,
                "Select a game to continue:", "Continue Game",
                JOptionPane.QUESTION_MESSAGE, null, gameOptions, gameOptions[0]);

        if (selected != null) {
            for (int i = 0; i < gameOptions.length; i++) {
                if (gameOptions[i].equals(selected)) {
                    gui.showGameScreen(activeGames.get(i));
                    break;
                }
            }
        }
    }

    private void showStatsDialog() {
        User currentUser = app.getCurrentUser();
        String stats = String.format(
                "Email: %s\n" +
                        "Total Points: %d\n" +
                        "Active Games: %d\n" +
                        "Total Games Played: %d",
                currentUser.getEmail(),
                currentUser.getPoints(),
                currentUser.getActiveGames().size(),
                currentUser.getGameIds().size()
        );

        JOptionPane.showMessageDialog(this, stats, "Account Statistics",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        app.logout();
        gui.showLoginScreen();
    }
}