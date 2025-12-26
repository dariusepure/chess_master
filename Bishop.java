public class Bishop extends Piece {
    public Bishop(Colors color, Position position) {
        super(color, position, new BishopMoveStrategy());
    }

    @Override
    public char getType() {
        return 'B';
    }
}