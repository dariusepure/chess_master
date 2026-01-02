import java.util.ArrayList;
import java.util.List;

public class Game {
    private int id;
    private Board board;
    private Player player1;
    private Player player2;
    private List<Move> history;
    private int currentPlayerIndex;
    private String currentPlayerColor;
    private List<GameObserver> observers = new ArrayList<>();

    public Game() {
        this.board = new Board();
        this.history = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.currentPlayerColor = Colors.WHITE.toString();
    }

    public Game(int id, Player player1, Player player2) {
        this.id = id;
        if (player1.getColor() == Colors.WHITE) {
            this.player1 = player1;
            this.player2 = player2;
        } else {
            this.player1 = player2;
            this.player2 = player1;
            this.player1.setColor(Colors.WHITE);
            this.player2.setColor(Colors.BLACK);
        }

        this.board = new Board();
        this.history = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.currentPlayerColor = Colors.WHITE.toString();
    }

    // ========== METODE GETTER PENTRU PLAYERS ==========

    public Player getPlayer1() {
        if (player1 != null) {
            player1.ensureCapturedPiecesInitialized();
        }
        return player1;
    }

    public Player getPlayer2() {
        if (player2 != null) {
            player2.ensureCapturedPiecesInitialized();
        }
        return player2;
    }

    // ========== METODA CRITICĂ PENTRU CAPTURED PIECES ==========

    public void ensureCapturedPiecesInitialized() {
        if (player1 != null) {
            player1.ensureCapturedPiecesInitialized();
        }
        if (player2 != null) {
            player2.ensureCapturedPiecesInitialized();
        }
    }

    // ========== RESTUL METODELOR ORIGINALE ==========

    public void setId(long id) {
        this.id = (int) id;
    }

    public void setPlayers(List<Player> players) {
        if (players != null && players.size() >= 2) {
            Player p1 = players.get(0);
            Player p2 = players.get(1);

            if (p1 != null && p2 != null) {
                if (p1.getColor() == Colors.WHITE) {
                    this.player1 = p1;
                    this.player2 = p2;
                } else if (p2.getColor() == Colors.WHITE) {
                    this.player1 = p2;
                    this.player2 = p1;
                } else {
                    this.player1 = p1;
                    this.player2 = p2;
                    this.player1.setColor(Colors.WHITE);
                    this.player2.setColor(Colors.BLACK);
                }
            }
        }
        ensureCapturedPiecesInitialized();
    }

    public void setCurrentPlayerColor(String color) {
        this.currentPlayerColor = color;
        if (color != null && player1 != null && player2 != null) {
            if (color.equalsIgnoreCase("WHITE") ||
                    (player1.getColor().toString().equalsIgnoreCase(color))) {
                this.currentPlayerIndex = 0;
            } else if (color.equalsIgnoreCase("BLACK") ||
                    (player2.getColor().toString().equalsIgnoreCase(color))) {
                this.currentPlayerIndex = 1;
            }
        }
    }

    public void setBoard(List<Piece> pieces) {
        if (board == null) {
            board = new Board();
        }
        board.clear();
        if (pieces != null) {
            for (Piece piece : pieces) {
                if (piece != null && piece.getPosition() != null) {
                    board.addPiece(piece, piece.getPosition());
                }
            }
        }
        updatePawnFirstMoveFlags();
    }

    public void setMoves(List<Move> moves) {
        this.history = moves != null ? new ArrayList<>(moves) : new ArrayList<>();
    }

    public void start() {
        if (board == null) {
            board = new Board();
        }
        this.board.initialize();
        this.history.clear();
        this.currentPlayerIndex = 0;
        this.currentPlayerColor = Colors.WHITE.toString();
        if (player1 != null) {
            player1.clearCapturedPieces();
            player1.setPoints(0);
        }
        if (player2 != null) {
            player2.clearCapturedPieces();
            player2.setPoints(0);
        }
        ensureCapturedPiecesInitialized();
        notifyObservers("Game started");
    }

    public void resume() {
        ensureCapturedPiecesInitialized();
        notifyObservers("Game resumed");
    }

    public void switchPlayer() {
        if (player1 != null && player2 != null) {
            this.currentPlayerIndex = 1 - this.currentPlayerIndex;
            this.currentPlayerColor = getCurrentPlayer().getColor().toString();
            notifyObservers("Player switched to " + getCurrentPlayer().getName());
        }
    }

    public boolean checkForCheckMate() {
        Player opponent = getOpponentPlayer();
        if (opponent == null || board == null) return false;

        boolean checkmate = board.isCheckmate(opponent.getColor());
        if (checkmate) {
            notifyObservers("Checkmate!");
        }
        return checkmate;
    }

    public boolean checkForStalemate() {
        Player opponent = getOpponentPlayer();
        if (opponent == null || board == null) return false;

        boolean stalemate = board.isStalemate(opponent.getColor());
        if (stalemate) {
            notifyObservers("Stalemate!");
        }
        return stalemate;
    }

    public void addMove(Player player, Position from, Position to) {
        if (player == null || from == null || to == null || board == null) {
            return;
        }

        Piece captured = board.getPieceAt(to);
        Move move = new Move(player.getColor(), from, to, captured);
        this.history.add(move);

        notifyMoveMade(move);
        if (captured != null) {
            notifyPieceCaptured(captured);
        }

        ensureCapturedPiecesInitialized();
    }

    private void updatePawnFirstMoveFlags() {
        if (board == null) {
            return;
        }

        for (ChessPair<Position, Piece> pair : board.getAllPieces()) {
            Piece piece = pair.getValue();
            if (piece instanceof Pawn) {
                Pawn pawn = (Pawn) piece;
                Position pos = pair.getKey();
                Colors color = pawn.getColor();

                if ((color == Colors.WHITE && pos.getY() != 2) ||
                        (color == Colors.BLACK && pos.getY() != 7)) {
                    pawn.setFirstMove(false);
                }
            }
        }
    }

    public void addObserver(GameObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    private void notifyMoveMade(Move move) {
        for (GameObserver observer : observers) {
            observer.onMoveMade(move);
        }
    }

    private void notifyPieceCaptured(Piece piece) {
        for (GameObserver observer : observers) {
            observer.onPieceCaptured(piece);
        }
    }

    private void notifyObservers(String message) {
        for (GameObserver observer : observers) {
            observer.onGameStateChanged(message);
        }
    }

    public Player getCurrentPlayer() {
        if (player1 == null || player2 == null) {
            return null;
        }
        ensureCapturedPiecesInitialized();
        return (currentPlayerIndex == 0) ? player1 : player2;
    }

    public Player getOpponentPlayer() {
        if (player1 == null || player2 == null) {
            return null;
        }
        ensureCapturedPiecesInitialized();
        return (currentPlayerIndex == 0) ? player2 : player1;
    }

    public Board getBoard() {
        return board;
    }

    public int getId() {
        return id;
    }

    public List<Move> getHistory() {
        return new ArrayList<>(history);
    }

    public String getCurrentPlayerColor() {
        return currentPlayerColor;
    }

    public void setPlayer1(Player player) {
        this.player1 = player;
        if (player != null && player.getColor() == null) {
            player.setColor(Colors.WHITE);
        }
        ensureCapturedPiecesInitialized();
    }

    public void setPlayer2(Player player) {
        this.player2 = player;
        if (player != null && player.getColor() == null) {
            player.setColor(Colors.BLACK);
        }
        ensureCapturedPiecesInitialized();
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public boolean isComputerTurn() {
        Player current = getCurrentPlayer();
        return current != null && "Computer".equals(current.getName());
    }

    public Player getHumanPlayer() {
        ensureCapturedPiecesInitialized();
        if (player1 != null && !"Computer".equals(player1.getName())) {
            return player1;
        } else if (player2 != null && !"Computer".equals(player2.getName())) {
            return player2;
        }
        return null;
    }

    public boolean isValidGame() {
        ensureCapturedPiecesInitialized();
        return player1 != null && player2 != null && board != null;
    }
}