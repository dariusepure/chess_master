import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenuFrame extends JFrame {
    private Main app;
    private ChessGUI gui;

    public MainMenuFrame(Main app, ChessGUI gui) {
        this.app = app;
        this.gui = gui;

        setTitle("Chess Master - Main Menu");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 240, 240));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(76, 175, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel welcomeLabel = new JLabel("Welcome to Chess Master", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel("User: " + app.getCurrentUser().getEmail(), SwingConstants.RIGHT);
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        userLabel.setForeground(Color.YELLOW);

        headerPanel.add(welcomeLabel, BorderLayout.CENTER);
        headerPanel.add(userLabel, BorderLayout.EAST);

        // Main buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(5, 1, 15, 15));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        buttonsPanel.setBackground(new Color(240, 240, 240));

        JButton newGameButton = createMenuButton("New Game");
        newGameButton.addActionListener(e -> gui.showNewGameScreen());

        // BUTONUL CORECTAT PENTRU CONTINUE GAME
        JButton continueGameButton = createMenuButton("Continue Game");
        continueGameButton.addActionListener(e -> {
            // Verifică dacă există jocuri salvate
            if (app.getCurrentUser().getActiveGames().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No saved games found!\nStart a new game first.",
                        "No Games",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                gui.showContinueGameScreen();
                dispose();
            }
        });

        JButton statsButton = createMenuButton("Statistics");
        statsButton.addActionListener(e -> showStatistics());

        JButton logoutButton = createMenuButton("Logout");
        logoutButton.addActionListener(e -> {
            app.logout();
            gui.showLoginScreen();
            dispose();
        });

        JButton exitButton = createMenuButton("Exit");
        exitButton.addActionListener(e -> gui.exit());

        buttonsPanel.add(newGameButton);
        buttonsPanel.add(continueGameButton);
        buttonsPanel.add(statsButton);
        buttonsPanel.add(logoutButton);
        buttonsPanel.add(exitButton);

        // Footer
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(245, 245, 245));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel pointsLabel = new JLabel("Total Points: " + app.getCurrentUser().getPoints());
        pointsLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel gamesLabel = new JLabel("Active Games: " + app.getCurrentUser().getActiveGames().size());
        gamesLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        footerPanel.add(pointsLabel, BorderLayout.WEST);
        footerPanel.add(gamesLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
        add(buttonsPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(new Color(76, 175, 80));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237));
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(76, 175, 80));
            }
        });

        return button;
    }

    private void showStatistics() {
        User user = app.getCurrentUser();
        String message = "User Statistics:\n\n" +
                "Email: " + user.getEmail() + "\n" +
                "Total Points: " + user.getPoints() + "\n" +
                "Active Games: " + user.getActiveGames().size() + "\n" +
                "Games History: " + user.getGameIds().size() + " games played";

        JOptionPane.showMessageDialog(this, message, "Statistics", JOptionPane.INFORMATION_MESSAGE);
    }
}