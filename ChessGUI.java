import javax.swing.*;

public class ChessGUI {
    private Main app;
    private JFrame currentFrame;

    public ChessGUI(Main app) {
        this.app = app;
    }

    // ================== SCREENS ==================

    public void showLoginScreen() {
        disposeCurrentFrame();
        currentFrame = new LoginFrame(app, this);
        currentFrame.setVisible(true);
    }

    public void showMainMenu() {
        disposeCurrentFrame();
        currentFrame = new MainMenuFrame(app, this);
        currentFrame.setVisible(true);
    }

    public void showNewGameScreen() {
        disposeCurrentFrame();
        currentFrame = new NewGameFrame(app, this);
        currentFrame.setVisible(true);
    }

    public void showContinueGameScreen() {
        disposeCurrentFrame();
        currentFrame = new ContinueGameFrame(app, this);
        currentFrame.setVisible(true);
    }

    public void showGameScreen(Game game) {
        disposeCurrentFrame();
        currentFrame = new GameFrame(app, game, this);
        currentFrame.setVisible(true);
    }

    // ADAUGAȚI ACEASTĂ METODĂ NOUĂ
    public void showGameOverScreen(Game game, String result, int bonusPoints) {
        // Calculează punctele din capturi
        int capturePoints = 0;

        // Determină care jucător este omul (nu computerul)
        Player humanPlayer = null;
        if (game.getPlayer1() != null && !game.getPlayer1().getName().equals("Computer")) {
            humanPlayer = game.getPlayer1();
        } else if (game.getPlayer2() != null && !game.getPlayer2().getName().equals("Computer")) {
            humanPlayer = game.getPlayer2();
        }

        if (humanPlayer != null) {
            capturePoints = humanPlayer.getPoints(); // Player.getPoints() returnează deja punctele din capturi
        }


        // Deschide fereastra cu TOATE punctele
        disposeCurrentFrame();
        currentFrame = new GameOverFrame(
                app,
                game,
                result,
                bonusPoints,    // Bonus joc (300, -300, 150, -150)
                capturePoints,  // Puncte din capturi
                this            // ChessGUI
        );
        currentFrame.setVisible(true);
    }

    public void exit() {
        disposeCurrentFrame();
        System.exit(0);
    }

    // ================== PROMOVARE PION ==================

    /**
     * Afiseaza dialogul de promovare pion
     * @return char tip piesa: Q, R, B, N
     */
    public char showPromotionDialog() {
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};

        int choice = JOptionPane.showOptionDialog(
                currentFrame,
                "Choose a piece for promotion:",
                "Pawn Promotion",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        return switch (choice) {
            case 0 -> 'Q';
            case 1 -> 'R';
            case 2 -> 'B';
            case 3 -> 'N';
            default -> 'Q'; // fallback dacă se închide dialogul
        };
    }

    // ================== UTILS ==================

    private void disposeCurrentFrame() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }
    }
}