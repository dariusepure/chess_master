import java.util.List;

public interface MoveStrategy {
    List<Position> getPossibleMoves(Board board, Position currentPos);
    boolean canCheckKing(Board board, Position currentPos, Position kingPos);
}