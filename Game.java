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

    private List<GameObserver> observers = new ArrayList<GameObserver>();

    public Game() {
        this.board = new Board();
        this.history = new ArrayList<Move>();
        this.currentPlayerIndex = 0;
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
        this.history = new ArrayList<Move>();
        this.currentPlayerIndex = 0;
        this.currentPlayerColor = Colors.WHITE.toString();
    }

    public void setId(long id) {
        this.id = (int) id;
    }

    public void setPlayers(List<Player> players) {
        if (players != null && players.size() >= 2) {
            if (players.get(0).getColor() == Colors.WHITE) {
                this.player1 = players.get(0);
                this.player2 = players.get(1);
            } else {
                this.player1 = players.get(1);
                this.player2 = players.get(0);
                this.player1.setColor(Colors.WHITE);
                this.player2.setColor(Colors.BLACK);
            }
        }
    }

    public void setCurrentPlayerColor(String color) {
        this.currentPlayerColor = color;
        if (color != null && player1 != null && player2 != null) {
            if (color.equalsIgnoreCase(player1.getColor().toString())) {
                this.currentPlayerIndex = 0;
            } else if (color.equalsIgnoreCase(player2.getColor().toString())) {
                this.currentPlayerIndex = 1;
            }
        }
    }

    public void setBoard(List<Piece> pieces) {
        if (board == null) {
            board = new Board();
        }

        board.clear();

        for (Piece piece : pieces) {
            board.addPiece(piece, piece.getPosition());
        }

        updatePawnFirstMoveFlags();
    }

    public void setMoves(List<Move> moves) {
        this.history = moves != null ? moves : new ArrayList<Move>();
    }

    public void start() {
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

        notifyObservers("Game started");
    }

    public void resume() {
        notifyObservers("Game resumed");
    }

    public void switchPlayer() {
        this.currentPlayerIndex = 1 - this.currentPlayerIndex;
        this.currentPlayerColor = getCurrentPlayer().getColor().toString();
        notifyObservers("Player switched to " + getCurrentPlayer().getName());
    }

    public boolean checkForCheckMate() {
        Player opponent = getOpponentPlayer();
        boolean checkmate = board.isCheckmate(opponent.getColor());
        if (checkmate) {
            notifyObservers("Checkmate!");
        }
        return checkmate;
    }

    public boolean checkForStalemate() {
        Player opponent = getOpponentPlayer();
        boolean stalemate = board.isStalemate(opponent.getColor());
        if (stalemate) {
            notifyObservers("Stalemate!");
        }
        return stalemate;
    }

    public void addMove(Player player, Position from, Position to) {
        Piece captured = board.getPieceAt(to);
        Move move = new Move(player.getColor(), from, to, captured);
        this.history.add(move);

        notifyMoveMade(move);
        if (captured != null) {
            notifyPieceCaptured(captured);
        }
    }

    private void updatePawnFirstMoveFlags() {
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

    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
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
        return (currentPlayerIndex == 0) ? player1 : player2;
    }

    public Player getOpponentPlayer() {
        return (currentPlayerIndex == 0) ? player2 : player1;
    }

    public Board getBoard() {
        return board;
    }

    public int getId() {
        return id;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public List<Move> getHistory() {
        return new ArrayList<Move>(history);
    }

    public String getCurrentPlayerColor() {
        return currentPlayerColor;
    }

    public void setPlayer1(Player player) {
        this.player1 = player;
    }

    public void setPlayer2(Player player) {
        this.player2 = player;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public boolean isComputerTurn() {
        return getCurrentPlayer().getName().equals("Computer");
    }

    public Player getHumanPlayer() {
        return (player1.getName().equals("Computer")) ? player2 : player1;
    }
}