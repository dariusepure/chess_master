import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ContinueGameFrame extends JFrame {
    private Main app;
    private ChessGUI gui;
    private JList<Game> gamesList;
    private DefaultListModel<Game> listModel;

    public ContinueGameFrame(Main app, ChessGUI gui) {
        this.app = app;
        this.gui = gui;
        setTitle("Chess Master");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        loadGames();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 240, 240));
        JLabel titleLabel = new JLabel("Continue Saved Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        titleLabel.setForeground(new Color(76, 175, 80));
        add(titleLabel, BorderLayout.NORTH);
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        mainPanel.setBackground(Color.WHITE);
        listModel = new DefaultListModel<>();
        gamesList = new JList<>(listModel);
        gamesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gamesList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gamesList.setBackground(Color.WHITE);
        gamesList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Game) {
                    Game game = (Game) value;
                    String text = "Game #" + game.getId() + " - ";
                    if (game.getPlayer1() != null && game.getPlayer2() != null) {
                        text += game.getPlayer1().getName() + " vs " + game.getPlayer2().getName();
                        text += " (" + game.getHistory().size() + " moves)";
                    } else {
                        text += "Invalid game data";
                    }
                    setText(text);
                    if (isSelected) {
                        setBackground(new Color(200, 230, 255));
                        setForeground(Color.BLACK);
                    }
                } else {
                    setText("No saved games found");
                    setForeground(Color.GRAY);
                }

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(gamesList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Select a game to continue"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        JButton continueButton = new JButton("Continue Game");
        continueButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        continueButton.setBackground(new Color(76, 175, 80));
        continueButton.setForeground(Color.WHITE);
        continueButton.addActionListener(e -> continueSelectedGame());
        JButton deleteButton = new JButton("Delete Game");
        deleteButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        deleteButton.setBackground(new Color(220, 80, 80));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> deleteSelectedGame());
        JButton backButton = new JButton("Back to Menu");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> {
            gui.showMainMenu();
            dispose();
        });
        buttonPanel.add(continueButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadGames() {
        listModel.clear();
        List<Game> allGames = new ArrayList<>(app.getAllGames().values());

        if (allGames.isEmpty()) {
            listModel.addElement(null);
        } else {
            for (Game game : allGames) {
                if (game != null && game.getPlayer1() != null && game.getPlayer2() != null) {
                    listModel.addElement(game);
                }
            }
            if (listModel.size() == 0) {
                listModel.addElement(null);
            }
        }
    }

    private void continueSelectedGame() {
        Object selected = gamesList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a game from the list!",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!(selected instanceof Game)) {
            JOptionPane.showMessageDialog(this,
                    "Invalid selection!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Game selectedGame = (Game) selected;
        if (selectedGame.getPlayer1() == null || selectedGame.getPlayer2() == null) {
            JOptionPane.showMessageDialog(this,
                    "This game has corrupted data. Please delete it.",
                    "Corrupted Game",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        gui.showGameScreen(selectedGame);
        dispose();
    }

    private void deleteSelectedGame() {
        Object selected = gamesList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a game to delete!",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!(selected instanceof Game)) {
            return;
        }
        Game selectedGame = (Game) selected;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete game #" + selectedGame.getId() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            app.getAllGames().remove(selectedGame.getId());
            User currentUser = app.getCurrentUser();
            if (currentUser != null) {
                currentUser.removeGame(selectedGame);
            }
            app.saveData();
            loadGames();
            JOptionPane.showMessageDialog(this,
                    "Game deleted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}