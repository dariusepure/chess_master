import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import javax.swing.JOptionPane;

public class Board {
    private TreeSet<ChessPair<Position, Piece>> pieces = new TreeSet<>();

    public void initialize() {
        pieces.clear();

        pieces.add(new ChessPair<>(new Position('A', 1), PieceFactory.createPiece('R', Colors.WHITE, new Position('A', 1))));
        pieces.add(new ChessPair<>(new Position('B', 1), PieceFactory.createPiece('N', Colors.WHITE, new Position('B', 1))));
        pieces.add(new ChessPair<>(new Position('C', 1), PieceFactory.createPiece('B', Colors.WHITE, new Position('C', 1))));
        pieces.add(new ChessPair<>(new Position('D', 1), PieceFactory.createPiece('Q', Colors.WHITE, new Position('D', 1))));
        pieces.add(new ChessPair<>(new Position('E', 1), PieceFactory.createPiece('K', Colors.WHITE, new Position('E', 1))));
        pieces.add(new ChessPair<>(new Position('F', 1), PieceFactory.createPiece('B', Colors.WHITE, new Position('F', 1))));
        pieces.add(new ChessPair<>(new Position('G', 1), PieceFactory.createPiece('N', Colors.WHITE, new Position('G', 1))));
        pieces.add(new ChessPair<>(new Position('H', 1), PieceFactory.createPiece('R', Colors.WHITE, new Position('H', 1))));

        for (char c = 'A'; c <= 'H'; c++) {
            pieces.add(new ChessPair<>(new Position(c, 2), PieceFactory.createPiece('P', Colors.WHITE, new Position(c, 2))));
            pieces.add(new ChessPair<>(new Position(c, 7), PieceFactory.createPiece('P', Colors.BLACK, new Position(c, 7))));
        }

        pieces.add(new ChessPair<>(new Position('A', 8), PieceFactory.createPiece('R', Colors.BLACK, new Position('A', 8))));
        pieces.add(new ChessPair<>(new Position('B', 8), PieceFactory.createPiece('N', Colors.BLACK, new Position('B', 8))));
        pieces.add(new ChessPair<>(new Position('C', 8), PieceFactory.createPiece('B', Colors.BLACK, new Position('C', 8))));
        pieces.add(new ChessPair<>(new Position('D', 8), PieceFactory.createPiece('Q', Colors.BLACK, new Position('D', 8))));
        pieces.add(new ChessPair<>(new Position('E', 8), PieceFactory.createPiece('K', Colors.BLACK, new Position('E', 8))));
        pieces.add(new ChessPair<>(new Position('F', 8), PieceFactory.createPiece('B', Colors.BLACK, new Position('F', 8))));
        pieces.add(new ChessPair<>(new Position('G', 8), PieceFactory.createPiece('N', Colors.BLACK, new Position('G', 8))));
        pieces.add(new ChessPair<>(new Position('H', 8), PieceFactory.createPiece('R', Colors.BLACK, new Position('H', 8))));
    }

    public void clear() {
        pieces.clear();
    }

    public void addPiece(Piece piece, Position position) {
        if (piece == null || position == null) return;
        piece.setPosition(position);
        pieces.add(new ChessPair<>(position, piece));
    }

    public void movePiece(Position from, Position to, Player movingPlayer, ChessGUI gui) throws InvalidMoveException {
        if (!isValidMove(from, to)) {
            throw new InvalidMoveException("Invalid move from " + from + " to " + to);
        }

        Piece movingPiece = getPieceAt(from);
        if (movingPiece == null) {
            throw new InvalidMoveException("No piece at " + from);
        }

        Piece targetPiece = getPieceAt(to);
        if (targetPiece != null && targetPiece.getType() == 'K') {
            throw new InvalidMoveException("Cannot capture the king");
        }

        ChessPair<Position, Piece> fromPair = findPairByPositionAndPiece(from, movingPiece);
        if (fromPair != null) pieces.remove(fromPair);

        if (targetPiece != null) {
            ChessPair<Position, Piece> targetPair = findPairByPositionAndPiece(to, targetPiece);
            if (targetPair != null) {
                pieces.remove(targetPair);
                if (movingPlayer != null && targetPiece.getColor() != movingPiece.getColor()) {
                    movingPlayer.addCapturedPiece(targetPiece);
                }
            }
        }

        movingPiece.setPosition(to);
        pieces.add(new ChessPair<>(to, movingPiece));

        if (movingPiece instanceof Pawn) {
            Pawn pawn = (Pawn) movingPiece;
            pawn.setFirstMove(false);
            int row = to.getY();
            boolean isWhite = pawn.getColor() == Colors.WHITE;

            if ((isWhite && row == 8) || (!isWhite && row == 1)) {
                // VERIFICĂ DACĂ E JUCĂTORUL OM SAU COMPUTER
                if (movingPlayer != null && "Computer".equals(movingPlayer.getName())) {
                    // COMPUTER - promovează automat în Regină
                    promotePawnAutomatically(to, pawn);
                } else {
                    // OM - arată dialog
                    promotePawnWithDialog(to, pawn, gui);
                }
            }
        }
    }

    private void promotePawnAutomatically(Position to, Pawn pawn) {
        ChessPair<Position, Piece> pawnPair = findPairByPositionAndPiece(to, pawn);
        if (pawnPair == null) return;

        pieces.remove(pawnPair);

        // Computerul întotdeauna alege Regina
        Piece promotedPiece = PieceFactory.createPiece(
                'Q',  // Întotdeauna Regină pentru computer
                pawn.getColor(),
                to
        );

        pieces.add(new ChessPair<>(to, promotedPiece));
        System.out.println("Computer pawn automatically promoted to Queen at " + to);
    }

    private void promotePawnWithDialog(Position to, Pawn pawn, ChessGUI gui) {
        ChessPair<Position, Piece> pawnPair = findPairByPositionAndPiece(to, pawn);
        if (pawnPair == null) return;

        // Folosește SwingUtilities pentru a rula pe thread-ul UI
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Object[] options = {"Queen", "Rook", "Bishop", "Knight"};
                int choice = JOptionPane.showOptionDialog(
                        null,
                        "Promote your pawn to:",
                        "Pawn Promotion",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]
                );

                char pieceChar = 'Q'; // default Queen
                switch (choice) {
                    case 0: pieceChar = 'Q'; break; // Queen
                    case 1: pieceChar = 'R'; break; // Rook
                    case 2: pieceChar = 'B'; break; // Bishop
                    case 3: pieceChar = 'N'; break; // Knight
                    default: pieceChar = 'Q'; break;
                }

                pieces.remove(pawnPair);
                Piece promotedPiece = PieceFactory.createPiece(
                        pieceChar,
                        pawn.getColor(),
                        to
                );
                pieces.add(new ChessPair<>(to, promotedPiece));


            }
        });
    }

    public boolean isValidMove(Position from, Position to) {
        if (to.getX() < 'A' || to.getX() > 'H' || to.getY() < 1 || to.getY() > 8) return false;
        if (from.equals(to)) return false;

        Piece piece = getPieceAt(from);
        if (piece == null) return false;

        Piece target = getPieceAt(to);
        if (target != null && target.getColor() == piece.getColor()) return false;

        List<Position> possibleMoves = piece.getPossibleMoves(this);
        if (!possibleMoves.contains(to)) return false;

        return !wouldLeaveKingInCheck(from, to, piece.getColor());
    }

    private boolean wouldLeaveKingInCheck(Position from, Position to, Colors playerColor) {
        Piece movingPiece = getPieceAt(from);
        if (movingPiece == null) return false;

        Piece targetPiece = getPieceAt(to);

        ChessPair<Position, Piece> fromPair = findPairByPositionAndPiece(from, movingPiece);
        if (fromPair != null) pieces.remove(fromPair);

        if (targetPiece != null) {
            ChessPair<Position, Piece> toPair = findPairByPositionAndPiece(to, targetPiece);
            if (toPair != null) pieces.remove(toPair);
        }

        movingPiece.setPosition(to);
        pieces.add(new ChessPair<>(to, movingPiece));

        Position kingPos = findKingPosition(playerColor);
        boolean inCheck = kingPos != null && isPositionAttacked(kingPos, playerColor);

        pieces.remove(new ChessPair<>(to, movingPiece));
        movingPiece.setPosition(from);
        pieces.add(new ChessPair<>(from, movingPiece));

        if (targetPiece != null) {
            pieces.add(new ChessPair<>(to, targetPiece));
        }

        return inCheck;
    }

    private Position findKingPosition(Colors color) {
        for (ChessPair<Position, Piece> pair : pieces) {
            if (pair.getValue().getType() == 'K' && pair.getValue().getColor() == color) {
                return pair.getKey();
            }
        }
        return null;
    }

    private boolean isPositionAttacked(Position pos, Colors defenderColor) {
        for (ChessPair<Position, Piece> pair : pieces) {
            Piece p = pair.getValue();
            if (p.getColor() != defenderColor && p.checkForCheck(this, pos)) {
                return true;
            }
        }
        return false;
    }

    public Piece getPieceAt(Position position) {
        for (ChessPair<Position, Piece> pair : pieces) {
            if (pair.getKey().equals(position)) {
                return pair.getValue();
            }
        }
        return null;
    }

    public List<ChessPair<Position, Piece>> getAllPieces() {
        return new ArrayList<>(pieces);
    }

    public boolean isCheckmate(Colors playerColor) {
        Position kingPosition = findKingPosition(playerColor);
        if (kingPosition == null) return false;
        if (!isPositionAttacked(kingPosition, playerColor)) return false;
        List<ChessPair<Position, Piece>> piecesCopy = new ArrayList<>(pieces);
        for (ChessPair<Position, Piece> pair : piecesCopy) {
            Piece piece = pair.getValue();
            if (piece.getColor() == playerColor) {
                List<Position> moves = piece.getPossibleMoves(this);
                for (Position move : moves) {
                    if (!wouldLeaveKingInCheck(pair.getKey(), move, playerColor)) return false;
                }
            }
        }
        return true;
    }

    public boolean isStalemate(Colors playerColor) {
        Position kingPosition = findKingPosition(playerColor);
        if (kingPosition == null) return false;
        if (isPositionAttacked(kingPosition, playerColor)) return false;
        List<ChessPair<Position, Piece>> piecesCopy = new ArrayList<>(pieces);
        for (ChessPair<Position, Piece> pair : piecesCopy) {
            Piece piece = pair.getValue();
            if (piece.getColor() == playerColor) {
                List<Position> moves = piece.getPossibleMoves(this);
                for (Position move : moves) {
                    if (!wouldLeaveKingInCheck(pair.getKey(), move, playerColor)) return false;
                }
            }
        }
        return true;
    }

    public boolean isInCheck(Colors playerColor) {
        Position kingPosition = findKingPosition(playerColor);
        if (kingPosition == null) return false;
        return isPositionAttacked(kingPosition, playerColor);
    }

    public List<Position> getValidMovesForPiece(Position position) {
        Piece piece = getPieceAt(position);
        if (piece == null) return new ArrayList<>();
        List<Position> possibleMoves = piece.getPossibleMoves(this);
        List<Position> validMoves = new ArrayList<>();
        for (Position move : possibleMoves) {
            if (isValidMove(position, move)) validMoves.add(move);
        }
        return validMoves;
    }

    private ChessPair<Position, Piece> findPairByPositionAndPiece(Position position, Piece piece) {
        for (ChessPair<Position, Piece> pair : pieces) {
            if (pair.getKey().equals(position) && pair.getValue() == piece) return pair;
        }
        return null;
    }
}