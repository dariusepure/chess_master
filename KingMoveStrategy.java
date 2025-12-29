import java.util.ArrayList;
import java.util.List;

public class KingMoveStrategy implements MoveStrategy {
    @Override
    public List<Position> getPossibleMoves(Board board, Position currentPos) {
        List<Position> moves = new ArrayList<Position>();
        int x = currentPos.getX() - 'A';
        int y = currentPos.getY() - 1;

        int[][] directions = {
                {-1,-1}, {-1,0}, {-1,1},
                {0,-1},          {0,1},
                {1,-1},  {1,0},  {1,1}
        };

        for (int i = 0; i < directions.length; i++) {
            int newX = x + directions[i][0];
            int newY = y + directions[i][1];

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
        return false;
    }

    private Colors getPieceColor(Board board, Position pos) {
        Piece piece = board.getPieceAt(pos);
        return piece != null ? piece.getColor() : null;
    }
}