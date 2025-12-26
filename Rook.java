public class Rook extends Piece {
    public Rook(Colors color, Position position) {
        super(color, position, new RookMoveStrategy());
    }

    @Override
    public char getType() {
        return 'R';
    }
}