public class Queen extends Piece {
    public Queen(Colors color, Position position) {
        super(color, position, new QueenMoveStrategy());
    }

    @Override
    public char getType() {
        return 'Q';
    }
}