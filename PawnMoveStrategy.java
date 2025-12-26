import java.util.ArrayList;
import java.util.List;

public class PawnMoveStrategy implements MoveStrategy {
    @Override
    public List<Position> getPossibleMoves(Board board, Position currentPos) {
        List<Position> moves = new ArrayList<Position>();
        int x = currentPos.getX() - 'A';
        int y = currentPos.getY() - 1;

        Piece pawn = board.getPieceAt(currentPos);
        if (!(pawn instanceof Pawn)) return moves;

        Colors color = ((Pawn) pawn).getColor();
        boolean isFirstMove = ((Pawn) pawn).isFirstMove();
        int direction = (color == Colors.WHITE) ? 1 : -1;

        // Mișcare înainte
        int forwardY = y + direction;
        if (forwardY >= 0 && forwardY < 8) {
            Position forwardPos = new Position((char)(x + 'A'), forwardY + 1);
            if (board.getPieceAt(forwardPos) == null) {
                moves.add(forwardPos);

                // Prima mutare - două pătrate
                if (isFirstMove) {
                    int doubleForwardY = y + 2 * direction;
                    if (doubleForwardY >= 0 && doubleForwardY < 8) {
                        Position doubleForwardPos = new Position((char)(x + 'A'), doubleForwardY + 1);
                        if (board.getPieceAt(doubleForwardPos) == null) {
                            moves.add(doubleForwardPos);
                        }
                    }
                }
            }
        }

        // Captură diagonală
        int[] captureX = {x - 1, x + 1};
        int captureY = y + direction;

        if (captureY >= 0 && captureY < 8) {
            for (int i = 0; i < captureX.length; i++) {
                int capX = captureX[i];
                if (capX >= 0 && capX < 8) {
                    Position capturePos = new Position((char)(capX + 'A'), captureY + 1);
                    Piece target = board.getPieceAt(capturePos);

                    if (target != null && target.getColor() != color) {
                        moves.add(capturePos);
                    }
                }
            }
        }

        return moves;
    }

    @Override
    public boolean canCheckKing(Board board, Position currentPos, Position kingPos) {
        Piece pawn = board.getPieceAt(currentPos);
        if (!(pawn instanceof Pawn)) return false;

        Colors color = ((Pawn) pawn).getColor();
        int x = currentPos.getX() - 'A';
        int y = currentPos.getY() - 1;
        int direction = (color == Colors.WHITE) ? 1 : -1;

        int[] captureX = {x - 1, x + 1};
        int captureY = y + direction;

        if (captureY >= 0 && captureY < 8) {
            for (int i = 0; i < captureX.length; i++) {
                int capX = captureX[i];
                if (capX >= 0 && capX < 8) {
                    Position capturePos = new Position((char)(capX + 'A'), captureY + 1);
                    if (capturePos.equals(kingPos)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private Colors getPieceColor(Board board, Position pos) {
        Piece piece = board.getPieceAt(pos);
        return piece != null ? piece.getColor() : null;
    }
}