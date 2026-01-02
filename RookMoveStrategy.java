import java.util.ArrayList;
import java.util.List;

public class RookMoveStrategy implements MoveStrategy {
    @Override
    public List<Position> getPossibleMoves(Board board, Position currentPos) {
        List<Position> moves = new ArrayList<Position>();
        int x = currentPos.getX() - 'A';
        int y = currentPos.getY() - 1;
        int[][] directions = {{0,1}, {0,-1}, {1,0}, {-1,0}};
        for (int i = 0; i < directions.length; i++) {
            for (int step = 1; step <= 7; step++) {
                int newX = x + directions[i][0] * step;
                int newY = y + directions[i][1] * step;
                if (newX < 0 || newX >= 8 || newY < 0 || newY >= 8) break;
                Position newPos = new Position((char)(newX + 'A'), newY + 1);
                Piece target = board.getPieceAt(newPos);
                if (target == null) {
                    moves.add(newPos);
                } else {
                    if (target.getColor() != getPieceColor(board, currentPos)) {
                        moves.add(newPos);
                    }
                    break;
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