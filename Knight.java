public class Knight extends Piece {
    public Knight(Colors color, Position position) {
        super(color, position, new KnightMoveStrategy());
    }

    @Override
    public char getType() {
        return 'N';
    }
}