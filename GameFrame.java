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

    // Componente UI
    private JLabel currentPlayerLabel;
    private JLabel capturedWhiteLabel;
    private JLabel capturedBlackLabel;
    private JLabel scoreLabel;
    private JLabel gameStatusLabel;
    private JTextArea movesHistoryArea;

    // Culori pentru tabla
    private final Color LIGHT_SQUARE = new Color(240, 217, 181);
    private final Color DARK_SQUARE = new Color(181, 136, 99);
    private final Color SELECTED_COLOR = new Color(144, 238, 144); // Verde deschis
    private final Color HIGHLIGHT_COLOR = new Color(255, 255, 153); // Galben

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

        // Dacă computerul începe (utilizatorul a ales negre)
        if (game.isComputerTurn() && !game.getCurrentPlayer().getName().equals("Computer")) {
            // Corectăm: Dacă utilizatorul e negru, computerul (alb) trebuie să mute primul
            Player human = game.getHumanPlayer();
            if (human.getColor() == Colors.BLACK) {
                // Computerul (alb) ar trebui să fie currentPlayer
                if (!game.getCurrentPlayer().getName().equals("Computer")) {
                    game.switchPlayer(); // Schimbă la computer
                }
            }
        }

        // Dacă e rândul computerului, face o mutare după o scurtă întârziere
        if (game.isComputerTurn()) {
            startComputerMove();
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 240, 240));

        // Header
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

        // Main content
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // Tabla de șah în centru
        mainPanel.add(createBoardPanel(), BorderLayout.CENTER);

        // Panel dreapta (informații și controale)
        mainPanel.add(createRightPanel(), BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);

        // Adaugă WindowListener pentru confirmare la închidere
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

                // Setare culoare pătrat
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

        // Informații jucători
        rightPanel.add(createPlayerInfoPanel());
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Piese capturate
        rightPanel.add(createCapturedPiecesPanel());
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Scor
        rightPanel.add(createScorePanel());
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Istoric mutări
        rightPanel.add(createMoveHistoryPanel());
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Butoane acțiuni
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
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Captured Pieces"));
        panel.setBackground(Color.WHITE);

        capturedWhiteLabel = new JLabel(" White: ");
        capturedBlackLabel = new JLabel(" Black: ");

        capturedWhiteLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        capturedBlackLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        panel.add(capturedWhiteLabel);
        panel.add(capturedBlackLabel);

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

        JButton hintButton = createActionButton("💡 Hint");
        hintButton.addActionListener(e -> showHint());

        JButton resignButton = createActionButton("🏳️ Resign");
        resignButton.addActionListener(e -> resignGame());

        JButton saveButton = createActionButton("💾 Save & Exit");
        saveButton.addActionListener(e -> saveAndExit());

        JButton menuButton = createActionButton("🏠 Main Menu");
        menuButton.addActionListener(e -> returnToMenu());

        panel.add(hintButton);
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
        // Dacă e rândul computerului, nu permite click-uri
        if (game.isComputerTurn()) {
            JOptionPane.showMessageDialog(this,
                    "Computer's turn! Please wait...",
                    "Not Your Turn", JOptionPane.WARNING_MESSAGE);
            return;
        }

        char x = (char) ('A' + col);
        int y = 8 - row; // Conversie coordonate
        Position position = new Position(x, y);

        // Obține jucătorul curent (uman)
        Player currentPlayer = game.getCurrentPlayer();

        if (selectedPosition == null) {
            // Selectare piesă
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
            // Mutare piesă
            if (highlightedMoves != null && highlightedMoves.contains(position)) {
                try {
                    // Efectuează mutarea
                    currentPlayer.makeMove(selectedPosition, position, game.getBoard());
                    game.addMove(currentPlayer, selectedPosition, position);

                    // Adaugă la istoric
                    addMoveToHistory(currentPlayer, selectedPosition, position);

                    // Verifică starea jocului
                    checkGameState();

                    // Schimbă jucătorul
                    game.switchPlayer();

                    // Dacă e acum rândul computerului
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

            // Resetare selecție
            selectedPosition = null;
            highlightedMoves = null;
            clearHighlights();
        }
    }

    private void startComputerMove() {
        // Folosește Timer pentru a simula "gândirea" computerului
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
            return; // Nu e rândul computerului
        }

        Board board = game.getBoard();
        List<Position> computerPieces = new ArrayList<>();

        // Găsește toate piesele computerului
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

        // Încearcă să găsească o mutare validă
        for (int attempt = 0; attempt < maxAttempts && !moveMade; attempt++) {
            Position from = computerPieces.get(rand.nextInt(computerPieces.size()));
            Piece piece = board.getPieceAt(from);

            if (piece == null) continue;

            List<Position> validMoves = board.getValidMovesForPiece(from);
            if (validMoves.isEmpty()) continue;

            Position to = validMoves.get(rand.nextInt(validMoves.size()));

            try {
                computer.makeMove(from, to, board);
                game.addMove(computer, from, to);

                // Adaugă la istoric
                addMoveToHistory(computer, from, to);

                moveMade = true;
                gameStatusLabel.setText("Computer moved: " + from + "-" + to);

                // Verifică starea jocului
                checkGameState();

                // Schimbă înapoi la jucătorul uman
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
        // Verifică șah-mat
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

        // Verifică pat
        if (game.checkForStalemate()) {
            JOptionPane.showMessageDialog(this,
                    "Stalemate! Draw game.",
                    "Draw", JOptionPane.INFORMATION_MESSAGE);

            Player human = game.getHumanPlayer();
            int points = human.getPoints(); // Doar punctele din capturi
            app.endGame(game, false); // Fără bonus/penalizare pentru pat
            gui.showGameOverScreen(game, "Stalemate - Draw", points);
            return;
        }

        // Verifică șah
        Board board = game.getBoard();
        Player currentPlayer = game.getCurrentPlayer();
        if (board.isInCheck(currentPlayer.getColor())) {
            gameStatusLabel.setText("CHECK! " + currentPlayer.getName() + "'s king is under attack!");
        }
    }

    private void addMoveToHistory(Player player, Position from, Position to) {
        String moveText = String.format("%s: %s-%s",
                player.getName().substring(0, Math.min(3, player.getName().length())),
                from, to);

        Piece captured = game.getBoard().getPieceAt(to);
        if (captured != null) {
            moveText += " (captures " + getPieceName(captured.getType()) + ")";
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
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                char x = (char) ('A' + col);
                int y = 8 - row;
                Position pos = new Position(x, y);

                if (selectedPosition != null && pos.equals(selectedPosition)) {
                    squares[row][col].setBackground(SELECTED_COLOR);
                } else if (highlightedMoves != null && highlightedMoves.contains(pos)) {
                    squares[row][col].setBackground(HIGHLIGHT_COLOR);
                } else {
                    if ((row + col) % 2 == 0) {
                        squares[row][col].setBackground(LIGHT_SQUARE);
                    } else {
                        squares[row][col].setBackground(DARK_SQUARE);
                    }
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
            }
        }
    }

    private void updateDisplay() {
        // Actualizează piesele pe tablă
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

        // Actualizează informații
        Player currentPlayer = game.getCurrentPlayer();
        currentPlayerLabel.setText("Turn: " + currentPlayer.getName());

        // Actualizează piese capturate
        updateCapturedPieces();

        // Actualizează scor
        scoreLabel.setText(game.getPlayer1().getPoints() + " - " + game.getPlayer2().getPoints());

        // Verifică șah
        Board board = game.getBoard();
        if (board.isInCheck(currentPlayer.getColor())) {
            gameStatusLabel.setText("CHECK! " + currentPlayer.getName() + "'s king is under attack!");
        } else {
            gameStatusLabel.setText(currentPlayer.getName() + "'s turn");
        }
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

    private void updateCapturedPieces() {
        StringBuilder whiteCaptured = new StringBuilder(" White: ");
        StringBuilder blackCaptured = new StringBuilder(" Black: ");

        // Verifică piesele capturate din istoric
        for (Move move : game.getHistory()) {
            if (move.getCapturedPiece() != null) {
                Piece captured = move.getCapturedPiece();
                String pieceSymbol = getPieceSymbol(captured);

                if (captured.getColor() == Colors.BLACK) {
                    whiteCaptured.append(pieceSymbol).append(" ");
                } else {
                    blackCaptured.append(pieceSymbol).append(" ");
                }
            }
        }

        capturedWhiteLabel.setText(whiteCaptured.toString());
        capturedBlackLabel.setText(blackCaptured.toString());
    }

    private void showHint() {
        if (game.isComputerTurn()) {
            JOptionPane.showMessageDialog(this,
                    "Wait for computer to move first!",
                    "Hint", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Player human = game.getCurrentPlayer();
        Board board = game.getBoard();
        List<Position> humanPieces = new ArrayList<>();

        // Găsește toate piesele umanului
        for (char x = 'A'; x <= 'H'; x++) {
            for (int y = 1; y <= 8; y++) {
                Position pos = new Position(x, y);
                Piece piece = board.getPieceAt(pos);
                if (piece != null && piece.getColor() == human.getColor()) {
                    humanPieces.add(pos);
                }
            }
        }

        if (!humanPieces.isEmpty()) {
            Random rand = new Random();
            Position randomPiece = humanPieces.get(rand.nextInt(humanPieces.size()));
            List<Position> validMoves = board.getValidMovesForPiece(randomPiece);

            if (!validMoves.isEmpty()) {
                Position suggestedMove = validMoves.get(rand.nextInt(validMoves.size()));
                JOptionPane.showMessageDialog(this,
                        "Hint: Try moving " + getPieceName(board.getPieceAt(randomPiece).getType()) +
                                " from " + randomPiece + " to " + suggestedMove,
                        "Hint", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "No valid moves for selected piece. Try another piece.",
                        "Hint", JOptionPane.INFORMATION_MESSAGE);
            }
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

    // Implementare GameObserver
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