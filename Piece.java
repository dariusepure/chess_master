import java.util.List;

public abstract class Piece implements ChessPiece {
    protected Colors color;
    protected Position position;
    protected MoveStrategy moveStrategy;

    public Piece(Colors color, Position position, MoveStrategy moveStrategy) {
        this.color = color;
        this.position = position;
        this.moveStrategy = moveStrategy;
    }

    public Colors getColor() {
        return color;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public List<Position> getPossibleMoves(Board board) {
        return moveStrategy.getPossibleMoves(board, position);
    }

    @Override
    public boolean checkForCheck(Board board, Position kingPosition) {
        return moveStrategy.canCheckKing(board, position, kingPosition);
    }

    protected boolean isValidPosition(char x, int y) {
        return x >= 'A' && x <= 'H' && y >= 1 && y <= 8;
    }

    protected boolean isOpponentPiece(Board board, Position p) {
        Piece piece = board.getPieceAt(p);
        return piece != null && piece.getColor() != this.color;
    }

    protected boolean isOwnPiece(Board board, Position p) {
        Piece piece = board.getPieceAt(p);
        return piece != null && piece.getColor() == this.color;
    }

    protected boolean isEmptySquare(Board board, Position p) {
        return board.getPieceAt(p) == null;
    }

    @Override
    public abstract char getType();
}