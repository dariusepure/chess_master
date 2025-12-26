import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private Colors color;
    private List<Piece> capturedPieces;
    private int points;

    public Player(String name, Colors color) {
        this.name = name;
        this.color = color;
        this.capturedPieces = new ArrayList<>();
        this.points = 0;
    }

    public void makeMove(Position from, Position to, Board board) throws InvalidMoveException {
        board.movePiece(from, to, this);
    }

    public void addCapturedPiece(Piece piece) {
        if (piece != null) {
            capturedPieces.add(piece);
            updatePointsFromCaptures();
        }
    }

    private void updatePointsFromCaptures() {
        points = 0;
        for (Piece piece : capturedPieces) {
            char type = piece.getType();
            if (type == 'Q') {
                points += 90;
            } else if (type == 'R') {
                points += 50;
            } else if (type == 'B' || type == 'N') {
                points += 30;
            } else if (type == 'P') {
                points += 10;
            }
        }
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void clearCapturedPieces() {
        capturedPieces.clear();
        points = 0;
    }

    public List<Piece> getCapturedPieces() {
        return capturedPieces;
    }

    public String getName() {
        return name;
    }

    public Colors getColor() {
        return color;
    }

    public void setColor(Colors color) {
        this.color = color;
    }
}