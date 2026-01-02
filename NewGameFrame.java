import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewGameFrame extends JFrame {
    private Main app;
    private ChessGUI gui;
    private JTextField playerNameField;
    private JComboBox<String> colorCombo;

    public NewGameFrame(Main app, ChessGUI gui) {
        this.app = app;
        this.gui = gui;

        setTitle("Chess - New Game");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel titleLabel = new JLabel("Start New Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.add(new JLabel("Player Name:"));
        playerNameField = new JTextField(app.getCurrentUser().getEmail());
        formPanel.add(playerNameField);
        formPanel.add(new JLabel("Your Color:"));
        colorCombo = new JComboBox<>(new String[]{"White", "Black"});
        formPanel.add(colorCombo);
        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(e -> startGame());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> gui.showMainMenu());
        formPanel.add(startButton);
        formPanel.add(backButton);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        //mainPanel.add(infoLabel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void startGame() {
        String playerName = playerNameField.getText().trim();
        if (playerName.isEmpty()) {
            playerName = app.getCurrentUser().getEmail();
        }
        Colors playerColor = (colorCombo.getSelectedIndex() == 0) ? Colors.WHITE : Colors.BLACK;
        Game game = app.createNewGame(playerName, playerColor);
        if (game != null) {
            gui.showGameScreen(game);
        }
    }
}