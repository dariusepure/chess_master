import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JsonReaderUtil {

    private JsonReaderUtil() {
    }

    public static List<User> readUsers(Path path) throws IOException, ParseException {
        List<User> result = new ArrayList<>();

        if (path == null || !Files.exists(path)) {
            return result;
        }

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JSONParser parser = new JSONParser();
            JSONArray arr = (JSONArray) parser.parse(reader);

            for (Object item : arr) {
                JSONObject obj = (JSONObject) item;

                User user = new User();
                user.setEmail((String) obj.get("email"));
                user.setPassword((String) obj.get("password"));

                Long pointsLong = (Long) obj.get("points");
                user.setPoints(pointsLong != null ? pointsLong.intValue() : 0);

                JSONArray gamesArray = (JSONArray) obj.get("games");
                List<Integer> gameIds = new ArrayList<>();
                if (gamesArray != null) {
                    for (Object gameId : gamesArray) {
                        gameIds.add(((Long) gameId).intValue());
                    }
                }
                user.setGameIds(gameIds);

                result.add(user);
            }
        }

        return result;
    }

    public static Map<Long, Game> readGamesAsMap(Path path) throws IOException, ParseException {
        Map<Long, Game> map = new HashMap<>();

        if (path == null || !Files.exists(path)) {
            return map;
        }

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JSONParser parser = new JSONParser();
            JSONArray arr = (JSONArray) parser.parse(reader);

            for (Object item : arr) {
                JSONObject obj = (JSONObject) item;

                long id = (Long) obj.get("id");

                Game game = new Game();
                game.setId(id);

                JSONArray playersArray = (JSONArray) obj.get("players");
                if (playersArray != null) {
                    List<Player> players = new ArrayList<>();
                    for (Object playerObj : playersArray) {
                        JSONObject playerJson = (JSONObject) playerObj;
                        String email = (String) playerJson.get("email");
                        String colorStr = (String) playerJson.get("color");
                        Colors color = Colors.valueOf(colorStr.toUpperCase());
                        players.add(new Player(email, color));
                    }
                    game.setPlayers(players);
                }

                String currentColor = (String) obj.get("currentPlayerColor");
                game.setCurrentPlayerColor(currentColor);

                JSONArray boardArray = (JSONArray) obj.get("board");
                if (boardArray != null) {
                    List<Piece> pieces = new ArrayList<>();
                    for (Object pieceObj : boardArray) {
                        JSONObject pieceJson = (JSONObject) pieceObj;

                        String type = (String) pieceJson.get("type");
                        String colorStr = (String) pieceJson.get("color");
                        String positionStr = (String) pieceJson.get("position");

                        Colors color = Colors.valueOf(colorStr.toUpperCase());
                        char x = positionStr.charAt(0);
                        int y = Character.getNumericValue(positionStr.charAt(1));
                        Position position = new Position(x, y);

                        Piece piece = createPiece(type.charAt(0), color, position);
                        if (piece != null) {
                            pieces.add(piece);
                        }
                    }
                    game.setBoard(pieces);
                }

                JSONArray movesArray = (JSONArray) obj.get("moves");
                if (movesArray != null) {
                    List<Move> moves = new ArrayList<>();
                    for (Object moveObj : movesArray) {
                        JSONObject moveJson = (JSONObject) moveObj;

                        String playerColorStr = (String) moveJson.get("playerColor");
                        String fromStr = (String) moveJson.get("from");
                        String toStr = (String) moveJson.get("to");

                        Move move = new Move(playerColorStr, fromStr, toStr);

                        JSONObject capturedJson = (JSONObject) moveJson.get("captured");
                        if (capturedJson != null) {
                            String capType = (String) capturedJson.get("type");
                            String capColorStr = (String) capturedJson.get("color");
                            Colors capColor = Colors.valueOf(capColorStr.toUpperCase());

                            char capX = toStr.charAt(0);
                            int capY = Character.getNumericValue(toStr.charAt(1));
                            Position capPos = new Position(capX, capY);

                            Piece captured = createPiece(capType.charAt(0), capColor, capPos);
                            if (captured != null) {
                                move = new Move(Colors.valueOf(playerColorStr.toUpperCase()),
                                        parsePosition(fromStr),
                                        parsePosition(toStr),
                                        captured);
                            }
                        }

                        moves.add(move);
                    }
                    game.setMoves(moves);
                }

                map.put(id, game);
            }
        }

        return map;
    }

    private static Piece createPiece(char type, Colors color, Position position) {
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
                return null;
        }
    }

    private static Position parsePosition(String posStr) {
        char x = posStr.charAt(0);
        int y = Character.getNumericValue(posStr.charAt(1));
        return new Position(x, y);
    }
}