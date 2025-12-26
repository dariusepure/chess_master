public class PieceFactory {
    public static Piece createPiece(char type, Colors color, Position position) {
        switch (type) {
            case 'K':
                return new King(color, position);
            case 'Q':
                return new Queen(color, position);
            case 'R':
                return new Rook(color, position);
            case 'B':
                return new Bishop(color, position);
            case 'N':
                return new Knight(color, position);
            case 'P':
                Pawn pawn = new Pawn(color, position);
                if ((color == Colors.WHITE && position.getY() != 2) ||
                        (color == Colors.BLACK && position.getY() != 7)) {
                    pawn.setFirstMove(false);
                }
                return pawn;
            default:
                throw new IllegalArgumentException("Invalid piece type: " + type);
        }
    }
}