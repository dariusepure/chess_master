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

    public void showGameOverScreen(Game game, String result, int points) {
        disposeCurrentFrame();
        currentFrame = new GameOverFrame(app, game, result, points, this);
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
