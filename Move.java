public class Move {
    private Colors playerColor;
    private Position from;
    private Position to;
    private Piece capturedPiece;

    public Move(String playerColor, String from, String to) {
        this.playerColor = Colors.valueOf(playerColor.toUpperCase());
        this.from = parsePosition(from);
        this.to = parsePosition(to);
        this.capturedPiece = null;
    }

    public Move(Colors playerColor, Position from, Position to, Piece capturedPiece) {
        this.playerColor = playerColor;
        this.from = from;
        this.to = to;
        this.capturedPiece = capturedPiece;
    }

    private Position parsePosition(String posStr) {
        if (posStr == null || posStr.length() != 2) {
            return null;
        }
        char x = posStr.charAt(0);
        int y = Character.getNumericValue(posStr.charAt(1));
        return new Position(x, y);
    }

    public Colors getPlayerColor() {
        return playerColor;
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public void setCapturedPiece(Piece capturedPiece) {
        this.capturedPiece = capturedPiece;
    }

    @Override
    public String toString() {
        String result = playerColor + ": " + from + "-" + to;
        if (capturedPiece != null) {
            result += " (captured " + capturedPiece.getType() + ")";
        }
        return result;
    }
}