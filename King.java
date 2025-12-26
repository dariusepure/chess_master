public class King extends Piece {
    public King(Colors color, Position position) {
        super(color, position, new KingMoveStrategy());
    }

    @Override
    public char getType() {
        return 'K';
    }
}