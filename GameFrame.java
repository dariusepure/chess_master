import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class GameFrame extends JFrame implements GameObserver {
    private Main app;
    private Game game;
    private ChessGUI gui;

    private JButton[][] squares = new JButton[8][8];
    private Position selectedPosition = null;
    private List<Position> highlightedMoves = null;

    private JLabel currentPlayerLabel;
    private JLabel capturedWhiteLabel;
    private JLabel capturedBlackLabel;
    private JLabel scoreLabel;
    private JLabel gameStatusLabel;
    private JTextArea movesHistoryArea;

    private final Color LIGHT_SQUARE = Color.WHITE;
    private final Color DARK_SQUARE = new Color(0, 102, 0);
    private final Color SELECTED_COLOR = new Color(144, 238, 144);
    private final Color HIGHLIGHT_COLOR = new Color(255, 255, 153);
    private final Color CAPTURE_COLOR = new Color(255, 100, 100);

    private Timer computerTimer;

    public GameFrame(Main app, Game game, ChessGUI gui) {
        this.app = app;
        this.game = game;
        this.gui = gui;

        setTitle("Chess Game #" + game.getId() + " - " +
                game.getPlayer1().getName() + " vs " + game.getPlayer2().getName());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        game.addObserver(this);
        initComponents();
        updateDisplay();

        if (game.isComputerTurn()) {
            startComputerMove();
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 240, 240));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        gameStatusLabel = new JLabel("Chess Game");
        gameStatusLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        gameStatusLabel.setForeground(Color.WHITE);

        currentPlayerLabel = new JLabel();
        currentPlayerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        currentPlayerLabel.setForeground(Color.YELLOW);

        headerPanel.add(gameStatusLabel, BorderLayout.WEST);
        headerPanel.add(currentPlayerLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(createBoardPanel(), BorderLayout.CENTER);
        mainPanel.add(createRightPanel(), BorderLayout.EAST);
        add(mainPanel, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
    }

    private JPanel createBoardPanel() {
        JPanel boardPanel = new JPanel(new GridLayout(8, 8));
        boardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        boardPanel.setBackground(Color.BLACK);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col] = new JButton();
                squares[row][col].setPreferredSize(new Dimension(70, 70));
                squares[row][col].setFont(new Font("Segoe UI Symbol", Font.BOLD, 32));
                squares[row][col].setFocusPainted(false);
                squares[row][col].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

                if ((row + col) % 2 == 0) {
                    squares[row][col].setBackground(LIGHT_SQUARE);
                } else {
                    squares[row][col].setBackground(DARK_SQUARE);
                }

                final int r = row;
                final int c = col;
                squares[row][col].addActionListener(e -> handleSquareClick(r, c));

                boardPanel.add(squares[row][col]);
            }
        }

        return boardPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rightPanel.setBackground(new Color(240, 240, 240));

        rightPanel.add(createPlayerInfoPanel());
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        rightPanel.add(createCapturedPiecesPanel());
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        rightPanel.add(createScorePanel());
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        rightPanel.add(createMoveHistoryPanel());
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        rightPanel.add(createActionButtonsPanel());

        return rightPanel;
    }

    private JPanel createPlayerInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Players"));
        panel.setBackground(Color.WHITE);

        Player player1 = game.getPlayer1();
        Player player2 = game.getPlayer2();

        JLabel player1Label = new JLabel(" " + player1.getName() + " (" + player1.getColor() + ")");
        player1Label.setFont(new Font("SansSerif", Font.BOLD, 14));
        player1Label.setOpaque(true);
        player1Label.setBackground(player1.getColor() == Colors.WHITE ? Color.WHITE : Color.LIGHT_GRAY);

        JLabel player2Label = new JLabel(" " + player2.getName() + " (" + player2.getColor() + ")");
        player2Label.setFont(new Font("SansSerif", Font.BOLD, 14));
        player2Label.setOpaque(true);
        player2Label.setBackground(player2.getColor() == Colors.WHITE ? Color.WHITE : Color.LIGHT_GRAY);

        panel.add(player1Label);
        panel.add(player2Label);

        return panel;
    }

    private JPanel createCapturedPiecesPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Captured Pieces"));
        panel.setBackground(Color.WHITE);

        JPanel whitePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        whitePanel.setBackground(Color.WHITE);
        JLabel whiteTitle = new JLabel("White: ");
        whiteTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        capturedWhiteLabel = new JLabel("");
        capturedWhiteLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        whitePanel.add(whiteTitle);
        whitePanel.add(capturedWhiteLabel);

        JPanel blackPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        blackPanel.setBackground(Color.WHITE);
        JLabel blackTitle = new JLabel("Black: ");
        blackTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        capturedBlackLabel = new JLabel("");
        capturedBlackLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        blackPanel.add(blackTitle);
        blackPanel.add(capturedBlackLabel);

        panel.add(whitePanel, BorderLayout.NORTH);
        panel.add(blackPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createScorePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Score"));
        panel.setBackground(Color.WHITE);

        scoreLabel = new JLabel("0 - 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        panel.add(scoreLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createMoveHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Move History"));
        panel.setBackground(Color.WHITE);

        movesHistoryArea = new JTextArea(10, 20);
        movesHistoryArea.setEditable(false);
        movesHistoryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        movesHistoryArea.setBackground(new Color(250, 250, 250));

        JScrollPane scrollPane = new JScrollPane(movesHistoryArea);
        scrollPane.setPreferredSize(new Dimension(250, 150));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createActionButtonsPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Actions"));
        panel.setBackground(Color.WHITE);



        JButton resignButton = createActionButton("🏳️ Resign");
        resignButton.addActionListener(e -> resignGame());

        JButton saveButton = createActionButton("💾 Save & Exit");
        saveButton.addActionListener(e -> saveAndExit());

        JButton menuButton = createActionButton("🏠 Main Menu");
        menuButton.addActionListener(e -> returnToMenu());

        panel.add(resignButton);
        panel.add(saveButton);
        panel.add(menuButton);

        return panel;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237));
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }
        });

        return button;
    }

    private void handleSquareClick(int row, int col) {
        if (game.isComputerTurn()) {
            JOptionPane.showMessageDialog(this,
                    "Computer's turn! Please wait...",
                    "Not Your Turn", JOptionPane.WARNING_MESSAGE);
            return;
        }

        char x = (char) ('A' + col);
        int y = 8 - row;
        Position position = new Position(x, y);

        Player currentPlayer = game.getCurrentPlayer();

        if (selectedPosition == null) {
            Piece piece = game.getBoard().getPieceAt(position);
            if (piece != null && piece.getColor() == currentPlayer.getColor()) {
                selectedPosition = position;
                highlightedMoves = game.getBoard().getValidMovesForPiece(position);
                highlightSquares();

                gameStatusLabel.setText("Selected: " + getPieceName(piece.getType()) + " at " + position);
            } else if (piece != null) {
                gameStatusLabel.setText("Not your piece! Select your own piece.");
            }
        } else {
            if (highlightedMoves != null && highlightedMoves.contains(position)) {
                try {
                    // === MODIFICARE AICI: Verifică dacă este captură înainte de mutare ===
                    Piece movingPiece = game.getBoard().getPieceAt(selectedPosition);
                    Piece targetPiece = game.getBoard().getPieceAt(position);
                    boolean isCapture = (targetPiece != null && movingPiece != null &&
                            targetPiece.getColor() != movingPiece.getColor());

                    currentPlayer.makeMove(selectedPosition, position, game.getBoard());
                    game.addMove(currentPlayer, selectedPosition, position);

                    // === CORECTAT: Pasează informația dacă este captură ===
                    addMoveToHistory(currentPlayer, selectedPosition, position, isCapture, targetPiece);

                    checkGameState();

                    game.switchPlayer();

                    if (game.isComputerTurn()) {
                        gameStatusLabel.setText("Computer thinking...");
                        startComputerMove();
                    } else {
                        updateDisplay();
                    }

                } catch (InvalidMoveException e) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid move: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                gameStatusLabel.setText("Invalid move target!");
            }

            selectedPosition = null;
            highlightedMoves = null;
            clearHighlights();
        }
    }

    private void startComputerMove() {
        if (computerTimer != null && computerTimer.isRunning()) {
            computerTimer.stop();
        }

        computerTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                computerTimer.stop();
                makeComputerMove();
            }
        });

        computerTimer.setRepeats(false);
        computerTimer.start();
    }

    private void makeComputerMove() {
        Player computer = game.getCurrentPlayer();
        if (!computer.getName().equals("Computer")) {
            return;
        }

        Board board = game.getBoard();
        List<Position> computerPieces = new ArrayList<>();

        for (char x = 'A'; x <= 'H'; x++) {
            for (int y = 1; y <= 8; y++) {
                Position pos = new Position(x, y);
                Piece piece = board.getPieceAt(pos);
                if (piece != null && piece.getColor() == computer.getColor()) {
                    computerPieces.add(pos);
                }
            }
        }

        if (computerPieces.isEmpty()) {
            gameStatusLabel.setText("Computer has no pieces!");
            return;
        }

        Random rand = new Random();
        boolean moveMade = false;
        int maxAttempts = 100;

        for (int attempt = 0; attempt < maxAttempts && !moveMade; attempt++) {
            Position from = computerPieces.get(rand.nextInt(computerPieces.size()));
            Piece piece = board.getPieceAt(from);

            if (piece == null) continue;

            List<Position> validMoves = board.getValidMovesForPiece(from);
            if (validMoves.isEmpty()) continue;

            Position to = validMoves.get(rand.nextInt(validMoves.size()));

            // Verifică dacă este captură pentru computer
            Piece targetPiece = board.getPieceAt(to);
            boolean isCapture = (targetPiece != null && targetPiece.getColor() != piece.getColor());

            try {
                computer.makeMove(from, to, board);
                game.addMove(computer, from, to);

                // === CORECTAT: Adaugă la istoric cu informația corectă ===
                addMoveToHistory(computer, from, to, isCapture, targetPiece);

                moveMade = true;
                gameStatusLabel.setText("Computer moved: " + from + "-" + to);

                checkGameState();

                game.switchPlayer();

                updateDisplay();

            } catch (InvalidMoveException e) {
                // Continuă să încerce alte mutări
            }
        }

        if (!moveMade) {
            gameStatusLabel.setText("Computer cannot move - stalemate?");
            checkGameState();
        }
    }

    private void checkGameState() {
        if (game.checkForCheckMate()) {
            Player winner = game.getCurrentPlayer();
            boolean humanWins = !winner.getName().equals("Computer");

            if (humanWins) {
                JOptionPane.showMessageDialog(this,
                        "Checkmate! You win!",
                        "Victory", JOptionPane.INFORMATION_MESSAGE);
                app.endGame(game, true);
                gui.showGameOverScreen(game, "Victory by Checkmate!", 300);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Checkmate! Computer wins!",
                        "Defeat", JOptionPane.WARNING_MESSAGE);
                app.endGame(game, false);
                gui.showGameOverScreen(game, "Defeat by Checkmate!", -300);
            }
            return;
        }

        if (game.checkForStalemate()) {
            JOptionPane.showMessageDialog(this,
                    "Stalemate! Draw game.",
                    "Draw", JOptionPane.INFORMATION_MESSAGE);

            Player human = game.getHumanPlayer();
            int points = human.getPoints();
            app.endGame(game, false);
            gui.showGameOverScreen(game, "Stalemate - Draw", points);
            return;
        }

        Board board = game.getBoard();
        Player currentPlayer = game.getCurrentPlayer();
        if (board.isInCheck(currentPlayer.getColor())) {
            gameStatusLabel.setText("CHECK! " + currentPlayer.getName() + "'s king is under attack!");
        }
    }

    // === METODĂ CORECTATĂ ===
    private void addMoveToHistory(Player player, Position from, Position to,
                                  boolean isCapture, Piece capturedPiece) {
        String moveText = String.format("%s: %s-%s",
                player.getName().substring(0, Math.min(3, player.getName().length())),
                from, to);

        // === CORECTAT: Afișează "captures" doar dacă este cu adevărat captură ===
        if (isCapture && capturedPiece != null) {
            moveText += " (captures " + getPieceName(capturedPiece.getType()) + ")";
        }

        movesHistoryArea.append(moveText + "\n");
        movesHistoryArea.setCaretPosition(movesHistoryArea.getDocument().getLength());
    }

    private String getPieceName(char type) {
        switch (type) {
            case 'K': return "King";
            case 'Q': return "Queen";
            case 'R': return "Rook";
            case 'B': return "Bishop";
            case 'N': return "Knight";
            case 'P': return "Pawn";
            default: return "Piece";
        }
    }

    private void highlightSquares() {
        Board board = game.getBoard();
        Piece selectedPiece = (selectedPosition != null) ? board.getPieceAt(selectedPosition) : null;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                char x = (char) ('A' + col);
                int y = 8 - row;
                Position pos = new Position(x, y);

                if (selectedPosition != null && pos.equals(selectedPosition)) {
                    squares[row][col].setBackground(SELECTED_COLOR);
                } else if (highlightedMoves != null && highlightedMoves.contains(pos)) {
                    Piece targetPiece = board.getPieceAt(pos);

                    if (targetPiece != null && selectedPiece != null &&
                            targetPiece.getColor() != selectedPiece.getColor()) {
                        squares[row][col].setBackground(CAPTURE_COLOR);
                        squares[row][col].setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                    } else {
                        squares[row][col].setBackground(HIGHLIGHT_COLOR);
                        squares[row][col].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                    }
                } else {
                    if ((row + col) % 2 == 0) {
                        squares[row][col].setBackground(LIGHT_SQUARE);
                    } else {
                        squares[row][col].setBackground(DARK_SQUARE);
                    }
                    squares[row][col].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                }
            }
        }
    }

    private void clearHighlights() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row + col) % 2 == 0) {
                    squares[row][col].setBackground(LIGHT_SQUARE);
                } else {
                    squares[row][col].setBackground(DARK_SQUARE);
                }
                squares[row][col].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            }
        }
    }

    private void updateDisplay() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                char x = (char) ('A' + col);
                int y = 8 - row;
                Position pos = new Position(x, y);
                Piece piece = game.getBoard().getPieceAt(pos);

                if (piece == null) {
                    squares[row][col].setText("");
                    squares[row][col].setToolTipText(null);
                } else {
                    squares[row][col].setText(getPieceSymbol(piece));
                    squares[row][col].setToolTipText(getPieceName(piece.getType()) +
                            " (" + piece.getColor() + ")");
                }
            }
        }

        Player currentPlayer = game.getCurrentPlayer();
        currentPlayerLabel.setText("Turn: " + currentPlayer.getName());

        updateCapturedPieces();

        scoreLabel.setText(game.getPlayer1().getPoints() + " - " + game.getPlayer2().getPoints());

        Board board = game.getBoard();
        if (board.isInCheck(currentPlayer.getColor())) {
            gameStatusLabel.setText("CHECK! " + currentPlayer.getName() + "'s king is under attack!");
        } else {
            gameStatusLabel.setText(currentPlayer.getName() + "'s turn");
        }
    }

    private void updateCapturedPieces() {
        Player whitePlayer = (game.getPlayer1().getColor() == Colors.WHITE) ?
                game.getPlayer1() : game.getPlayer2();
        Player blackPlayer = (whitePlayer == game.getPlayer1()) ?
                game.getPlayer2() : game.getPlayer1();

        String whiteCapturedStr = blackPlayer.getCapturedPiecesString();
        String blackCapturedStr = whitePlayer.getCapturedPiecesString();

        capturedWhiteLabel.setText(whiteCapturedStr);
        capturedBlackLabel.setText(blackCapturedStr);
    }

    private String getPieceSymbol(Piece piece) {
        char type = piece.getType();
        boolean isWhite = piece.getColor() == Colors.WHITE;

        switch (type) {
            case 'K': return isWhite ? "♔" : "♚";
            case 'Q': return isWhite ? "♕" : "♛";
            case 'R': return isWhite ? "♖" : "♜";
            case 'B': return isWhite ? "♗" : "♝";
            case 'N': return isWhite ? "♘" : "♞";
            case 'P': return isWhite ? "♙" : "♟";
            default: return "";
        }
    }



    private void resignGame() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to resign? You will lose 150 points.",
                "Confirm Resignation", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            app.resignGame(game);
            gui.showGameOverScreen(game, "Resignation", -150);
        }
    }

    private void saveAndExit() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Save game and return to main menu?",
                "Save Game", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            app.saveGame(game);
            gui.showMainMenu();
        }
    }

    private void returnToMenu() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Return to main menu? Unsaved progress will be lost.",
                "Confirm Exit", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            gui.showMainMenu();
        }
    }

    private void confirmExit() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?",
                "Confirm Exit", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            gui.showMainMenu();
        }
    }

    @Override
    public void onMoveMade(Move move) {
        updateDisplay();
    }

    @Override
    public void onPieceCaptured(Piece piece) {
        updateCapturedPieces();
        updateDisplay();
    }

    @Override
    public void onGameStateChanged(String state) {
        gameStatusLabel.setText(state);
    }
}