public interface GameObserver {
    void onMoveMade(Move move);
    void onPieceCaptured(Piece piece);
    void onGameStateChanged(String state);
}