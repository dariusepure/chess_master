public class Pawn extends Piece {
    private boolean firstMove;

    public Pawn(Colors color, Position position) {
        super(color, position, new PawnMoveStrategy());
        this.firstMove = isOnStartingPosition(position, color);
    }

    private boolean isOnStartingPosition(Position position, Colors color) {
        int y = position.getY();
        if (color == Colors.WHITE) {
            return y == 2;
        } else {
            return y == 7;
        }
    }

    @Override
    public char getType() {
        return 'P';
    }

    public void setFirstMove(boolean firstMove) {
        this.firstMove = firstMove;
    }

    public boolean isFirstMove() {
        return firstMove;
    }
}