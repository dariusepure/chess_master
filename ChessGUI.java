import javax.swing.*;

public class ChessGUI {
    private Main app;
    private JFrame currentFrame;

    public ChessGUI(Main app) {
        this.app = app;
    }

    public void showLoginScreen() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        currentFrame = new LoginFrame(app, this);
        currentFrame.setVisible(true);
    }

    public void showMainMenu() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        currentFrame = new MainMenuFrame(app, this);
        currentFrame.setVisible(true);
    }

    public void showNewGameScreen() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        currentFrame = new NewGameFrame(app, this);
        currentFrame.setVisible(true);
    }

    // METODĂ NOUĂ PENTRU CONTINUE GAME
    public void showContinueGameScreen() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        currentFrame = new ContinueGameFrame(app, this);
        currentFrame.setVisible(true);
    }

    public void showGameScreen(Game game) {
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        currentFrame = new GameFrame(app, game, this);
        currentFrame.setVisible(true);
    }

    public void showGameOverScreen(Game game, String result, int points) {
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        currentFrame = new GameOverFrame(app, game, result, points, this);
        currentFrame.setVisible(true);
    }

    public void exit() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        System.exit(0);
    }
}