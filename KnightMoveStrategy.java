import java.util.ArrayList;
import java.util.List;

public class KnightMoveStrategy implements MoveStrategy {
    @Override
    public List<Position> getPossibleMoves(Board board, Position currentPos) {
        List<Position> moves = new ArrayList<Position>();
        int x = currentPos.getX() - 'A';
        int y = currentPos.getY() - 1;

        int[][] knightMoves = {
                {-2,-1}, {-2,1},
                {-1,-2}, {-1,2},
                {1,-2},  {1,2},
                {2,-1},  {2,1}
        };

        for (int i = 0; i < knightMoves.length; i++) {
            int newX = x + knightMoves[i][0];
            int newY = y + knightMoves[i][1];

            if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                Position newPos = new Position((char)(newX + 'A'), newY + 1);
                Piece target = board.getPieceAt(newPos);

                if (target == null || target.getColor() != getPieceColor(board, currentPos)) {
                    moves.add(newPos);
                }
            }
        }

        return moves;
    }

    @Override
    public boolean canCheckKing(Board board, Position currentPos, Position kingPos) {
        List<Position> moves = getPossibleMoves(board, currentPos);
        return moves.contains(kingPos);
    }

    private Colors getPieceColor(Board board, Position pos) {
        Piece piece = board.getPieceAt(pos);
        return piece != null ? piece.getColor() : null;
    }
}