import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private Colors color;
    private int points;
    private List<Piece> capturedPieces;

    public Player(String name, Colors color) {
        this.name = (name == null || name.trim().isEmpty()) ? "Unknown" : name.trim();
        this.color = (color == null) ? Colors.WHITE : color;
        this.points = 0;
        this.capturedPieces = new ArrayList<>();
    }

    public Player() {
        this("Unknown", Colors.WHITE);
    }

    public void makeMove(Position from, Position to, Board board) throws InvalidMoveException {
        if (board == null) {
            throw new InvalidMoveException("Board is null");
        }

        if (from == null || to == null) {
            throw new InvalidMoveException("Invalid positions");
        }

        Piece piece = board.getPieceAt(from);
        if (piece == null) {
            throw new InvalidMoveException("No piece at " + from);
        }

        if (piece.getColor() != this.color) {
            throw new InvalidMoveException("Piece at " + from + " does not belong to you");
        }

        board.movePiece(from, to, this);
    }

    public void addCapturedPiece(Piece originalPiece) {
        if (originalPiece == null) {
            return;
        }

        if (originalPiece.getColor() == this.color) {
            return;
        }

        Piece capturedCopy = createPieceCopy(originalPiece);

        capturedPieces.add(capturedCopy);
        updatePoints();
    }

    private Piece createPieceCopy(Piece original) {
        char type = original.getType();
        Colors color = original.getColor();

        Position capturePosition = new Position('X', 0);

        Piece copy = PieceFactory.createPiece(type, color, capturePosition);

        if (copy instanceof Pawn) {
            ((Pawn) copy).setFirstMove(false);
        }

        return copy;
    }

    private void updatePoints() {
        this.points = 0;

        for (Piece piece : capturedPieces) {
            this.points += getPieceValue(piece.getType());
        }
    }

    private int getPieceValue(char type) {
        switch (type) {
            case 'Q': return 90;
            case 'R': return 50;
            case 'B': return 30;
            case 'N': return 30;
            case 'P': return 10;
            default:  return 0;
        }
    }

    public List<String> getCapturedPiecesForJson() {
        List<String> result = new ArrayList<>();
        for (Piece piece : capturedPieces) {
            result.add(piece.getType() + ":" + piece.getColor());
        }
        return result;
    }

    public void loadCapturedPiecesFromJson(List<String> capturedData) {
        capturedPieces.clear();
        points = 0;

        if (capturedData != null) {
            for (String pieceStr : capturedData) {
                try {
                    String[] parts = pieceStr.split(":");
                    if (parts.length == 2) {
                        char type = parts[0].charAt(0);
                        Colors color = Colors.valueOf(parts[1].toUpperCase());

                        Position neutralPos = new Position('X', 0);
                        Piece piece = PieceFactory.createPiece(type, color, neutralPos);

                        if (piece != null) {
                            capturedPieces.add(piece);
                            points += getPieceValue(type);
                        }
                    }
                } catch (Exception e) {
                    // Ignoră erorile la încărcare
                }
            }
        }
    }

    public String getCapturedPiecesString() {
        if (capturedPieces.isEmpty()) {
            return "None";
        }

        StringBuilder sb = new StringBuilder();
        for (Piece piece : capturedPieces) {
            sb.append(getPieceSymbol(piece)).append(" ");
        }
        return sb.toString().trim();
    }

    private String getPieceSymbol(Piece piece) {
        if (piece == null) return "?";

        boolean isWhitePiece = piece.getColor() == Colors.WHITE;

        switch (piece.getType()) {
            case 'Q': return isWhitePiece ? "♕" : "♛";
            case 'R': return isWhitePiece ? "♖" : "♜";
            case 'B': return isWhitePiece ? "♗" : "♝";
            case 'N': return isWhitePiece ? "♘" : "♞";
            case 'P': return isWhitePiece ? "♙" : "♟";
            default:  return "?";
        }
    }

    public String getName() { return name; }
    public Colors getColor() { return color; }
    public int getPoints() { return points; }
    public List<Piece> getCapturedPieces() { return new ArrayList<>(capturedPieces); }
    public int getCapturedCount() { return capturedPieces.size(); }

    public void setName(String name) { this.name = name; }
    public void setColor(Colors color) { this.color = color; }
    public void setPoints(int points) { this.points = points; }

    public void clearCapturedPieces() {
        capturedPieces.clear();
        points = 0;
    }

    public void resetPlayer() {
        clearCapturedPieces();
    }

    @Override
    public String toString() {
        return name + " (" + color + ") - " + points + " pts";
    }
}