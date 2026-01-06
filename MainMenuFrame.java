import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenuFrame extends JFrame {
    private Main app;
    private ChessGUI gui;

    public MainMenuFrame(Main app, ChessGUI gui) {
        this.app = app;
        this.gui = gui;
        setTitle("Chess Master");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 240, 240));
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
        JPanel buttonsPanel = new JPanel(new GridLayout(5, 1, 15, 15));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        buttonsPanel.setBackground(new Color(240, 240, 240));
        JButton newGameButton = createStyledButton("New Game", true);
        newGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gui.showNewGameScreen();
            }
        });
        JButton continueGameButton = createStyledButton("Continue Game", true);
        continueGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (app.getCurrentUser().getActiveGames().isEmpty()) {
                    JOptionPane.showMessageDialog(MainMenuFrame.this,
                            "No saved games found!\nStart a new game first.",
                            "No Games",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    gui.showContinueGameScreen();
                    dispose();
                }
            }
        });
        JButton logoutButton = createStyledButton("Logout", true);
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                app.logout();
                gui.showLoginScreen();
                dispose();
            }
        });
        JButton exitButton = createStyledButton("Exit", true);
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gui.exit();
            }
        });
        buttonsPanel.add(newGameButton);
        buttonsPanel.add(continueGameButton);
        buttonsPanel.add(logoutButton);
        buttonsPanel.add(exitButton);
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

    private JButton createStyledButton(String text, boolean largeFont) {
        final JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, largeFont ? 16 : 14));
        button.setBackground(new Color(76, 175, 80));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(56, 142, 60));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(76, 175, 80));
            }
        });
        return button;
    }
}