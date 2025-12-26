import java.util.List;
public interface ChessPiece {
    List<Position> getPossibleMoves(Board board);
    boolean checkForCheck(Board board, Position kingPosition);
    char getType();
    Colors getColor();
    Position getPosition();
    void setPosition(Position position);
}