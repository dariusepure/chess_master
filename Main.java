import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    private static Main instance;
    private List<User> users = new ArrayList<>();
    private Map<Integer, Game> allGames = new HashMap<>();
    private User currentUser = null;
    private ChessGUI gui;
    private int nextGameId = 1;
    private static final String ACCOUNTS_FILE = "accounts.json";
    private static final String GAMES_FILE = "games.json";

    private Main() {
        loadData();
    }

    public static Main getInstance() {
        if (instance == null) {
            instance = new Main();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public Map<Integer, Game> getAllGames() {
        return new HashMap<>(allGames);
    }

    public void logout() {
        currentUser = null;
        saveData();
    }

    private void loadData() {
        try {
            if (Files.exists(Paths.get(ACCOUNTS_FILE))) {
                List<User> loadedUsers = JsonReaderUtil.readUsers(Paths.get(ACCOUNTS_FILE));
                users.clear();
                users.addAll(loadedUsers);
            }
            if (Files.exists(Paths.get(GAMES_FILE))) {
                Map<Long, Game> gamesMap = JsonReaderUtil.readGamesAsMap(Paths.get(GAMES_FILE));
                allGames.clear();
                for (Map.Entry<Long, Game> entry : gamesMap.entrySet()) {
                    int gameId = entry.getKey().intValue();
                    Game game = entry.getValue();
                    if (game != null && game.getPlayer1() != null && game.getPlayer2() != null) {
                        game.ensureCapturedPiecesInitialized();
                        allGames.put(gameId, game);
                        if (gameId >= nextGameId) {
                            nextGameId = gameId + 1;
                        }
                    }
                }
                for (User user : users) {
                    List<Game> userGames = new ArrayList<>();
                    for (Integer gameId : user.getGameIds()) {
                        Game game = allGames.get(gameId);
                        if (game != null) {
                            userGames.add(game);
                        }
                    }
                    user.setGames(userGames);
                }
            }
        } catch (Exception e) {
        }
    }

    public void saveData() {
        try {
            saveValidGames();
            saveUsersManually();
        } catch (Exception e) {
        }
    }

    private void saveValidGames() {
        try {
            org.json.simple.JSONArray gamesArray = new org.json.simple.JSONArray();
            for (Map.Entry<Integer, Game> entry : allGames.entrySet()) {
                Game game = entry.getValue();
                if (game == null || game.getPlayer1() == null || game.getPlayer2() == null) {
                    continue;
                }
                org.json.simple.JSONObject gameObj = new org.json.simple.JSONObject();
                gameObj.put("id", game.getId());
                org.json.simple.JSONArray playersArray = new org.json.simple.JSONArray();
                org.json.simple.JSONObject player1Obj = new org.json.simple.JSONObject();
                player1Obj.put("email", game.getPlayer1().getName());
                player1Obj.put("color", game.getPlayer1().getColor().toString());
                player1Obj.put("points", game.getPlayer1().getPoints());
                org.json.simple.JSONArray captured1Array = new org.json.simple.JSONArray();
                for (String captured : game.getPlayer1().getCapturedPiecesForJson()) {
                    captured1Array.add(captured);
                }
                player1Obj.put("captured", captured1Array);
                org.json.simple.JSONObject player2Obj = new org.json.simple.JSONObject();
                player2Obj.put("email", game.getPlayer2().getName());
                player2Obj.put("color", game.getPlayer2().getColor().toString());
                player2Obj.put("points", game.getPlayer2().getPoints());
                org.json.simple.JSONArray captured2Array = new org.json.simple.JSONArray();
                for (String captured : game.getPlayer2().getCapturedPiecesForJson()) {
                    captured2Array.add(captured);
                }
                player2Obj.put("captured", captured2Array);
                playersArray.add(player1Obj);
                playersArray.add(player2Obj);
                gameObj.put("players", playersArray);
                gameObj.put("currentPlayerColor", game.getCurrentPlayerColor());
                org.json.simple.JSONArray boardArray = new org.json.simple.JSONArray();
                if (game.getBoard() != null) {
                    for (ChessPair<Position, Piece> pair : game.getBoard().getAllPieces()) {
                        org.json.simple.JSONObject pieceObj = new org.json.simple.JSONObject();
                        pieceObj.put("type", String.valueOf(pair.getValue().getType()));
                        pieceObj.put("color", pair.getValue().getColor().toString());
                        pieceObj.put("position", pair.getKey().toString());
                        boardArray.add(pieceObj);
                    }
                }
                gameObj.put("board", boardArray);
                org.json.simple.JSONArray movesArray = new org.json.simple.JSONArray();
                for (Move move : game.getHistory()) {
                    org.json.simple.JSONObject moveObj = new org.json.simple.JSONObject();
                    moveObj.put("playerColor", move.getPlayerColor().toString());
                    moveObj.put("from", move.getFrom().toString());
                    moveObj.put("to", move.getTo().toString());
                    if (move.getCapturedPiece() != null) {
                        org.json.simple.JSONObject capturedObj = new org.json.simple.JSONObject();
                        capturedObj.put("type", String.valueOf(move.getCapturedPiece().getType()));
                        capturedObj.put("color", move.getCapturedPiece().getColor().toString());
                        moveObj.put("captured", capturedObj);
                    }
                    movesArray.add(moveObj);
                }
                gameObj.put("moves", movesArray);
                gamesArray.add(gameObj);
            }
            try (java.io.FileWriter file = new java.io.FileWriter(GAMES_FILE)) {
                file.write(gamesArray.toJSONString());
                file.flush();
            }
        } catch (Exception e) {
        }
    }

    private void saveUsersManually() {
        try {
            org.json.simple.JSONArray usersArray = new org.json.simple.JSONArray();
            for (User user : users) {
                org.json.simple.JSONObject userObj = new org.json.simple.JSONObject();
                userObj.put("email", user.getEmail());
                userObj.put("password", user.getPassword());
                userObj.put("points", user.getPoints());
                org.json.simple.JSONArray gamesArray = new org.json.simple.JSONArray();
                for (Integer gameId : user.getGameIds()) {
                    if (allGames.containsKey(gameId)) {
                        gamesArray.add(gameId);
                    }
                }
                userObj.put("games", gamesArray);
                usersArray.add(userObj);
            }
            try (java.io.FileWriter file = new java.io.FileWriter(ACCOUNTS_FILE)) {
                file.write(usersArray.toJSONString());
                file.flush();
            }
        } catch (Exception e) {
        }
    }

    public Game createNewGame(String playerName, Colors playerColor) {
        Colors computerColor = (playerColor == Colors.WHITE) ? Colors.BLACK : Colors.WHITE;
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Player1";
        }
        Player humanPlayer = new Player(playerName, playerColor);
        Player computerPlayer = new Player("Computer", computerColor);
        Game game = new Game(nextGameId, humanPlayer, computerPlayer);
        game.start();
        allGames.put(nextGameId, game);
        if (currentUser != null) {
            currentUser.addGame(game);
        }
        nextGameId++;
        saveData();
        return game;
    }

    public void saveGame(Game game) {
        if (game != null && game.getPlayer1() != null && game.getPlayer2() != null) {
            game.ensureCapturedPiecesInitialized();
            allGames.put(game.getId(), game);
            saveData();
        }
    }

    public void resignGame(Game game) {
        if (game == null || game.getPlayer1() == null) {
            return;
        }
        Player humanPlayer = getHumanPlayer(game);
        int capturedPoints = humanPlayer.getPoints();
        int penalty = -150;
        int totalPoints = capturedPoints + penalty;
        if (currentUser != null) {
            currentUser.updatePoints(totalPoints);
            currentUser.removeGame(game);
        }
        allGames.remove(game.getId());
        saveData();
    }

    public void endGame(Game game, boolean humanWins) {
        if (game == null || game.getPlayer1() == null) {
            return;
        }
        Player humanPlayer = getHumanPlayer(game);
        if (humanWins) {
            int capturedPoints = humanPlayer.getPoints();
            int bonus = 300;
            int totalPoints = capturedPoints + bonus;
            if (currentUser != null) {
                currentUser.updatePoints(totalPoints);
            }
        } else {
            int capturedPoints = humanPlayer.getPoints();
            int penalty = -300;
            int totalPoints = capturedPoints + penalty;
            if (currentUser != null) {
                currentUser.updatePoints(totalPoints);
            }
        }
        if (currentUser != null) {
            currentUser.removeGame(game);
        }
        allGames.remove(game.getId());
        saveData();
    }

    private Player getHumanPlayer(Game game) {
        if (game.getPlayer1() == null || game.getPlayer2() == null) {
            return new Player("DefaultPlayer", Colors.WHITE);
        }
        return (game.getPlayer1().getName().equals("Computer")) ?
                game.getPlayer2() : game.getPlayer1();
    }

    public User login(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            showMessage("Empty email", "Error");
            return null;
        }
        if (!isValidEmail(email)) {
            showMessage("Invalid email format", "Error");
            return null;
        }
        email = email.trim().toLowerCase();
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                if (user.checkPassword(password)) {
                    currentUser = user;
                    showMessage("Logged in as: " + user.getEmail(), "Success");
                    return user;
                } else {
                    showMessage("Wrong password", "Error");
                    return null;
                }
            }
        }
        showMessage("Account does not exist", "Error");
        return null;
    }

    public User newAccount(String email, String password, String confirmPassword) {
        if (email == null || email.trim().isEmpty()) {
            showMessage("Empty email", "Error");
            return null;
        }
        if (!isValidEmail(email)) {
            showMessage("Invalid email format", "Error");
            return null;
        }
        if (password == null || password.trim().isEmpty()) {
            showMessage("Empty password", "Error");
            return null;
        }
        if (!password.equals(confirmPassword)) {
            showMessage("Passwords do not match", "Error");
            return null;
        }
        email = email.trim().toLowerCase();
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                showMessage("Email already used", "Error");
                return null;
            }
        }
        User newUser = new User(email, password);
        users.add(newUser);
        currentUser = newUser;
        showMessage("Account created for: " + email, "Success");
        saveData();
        return newUser;
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private void showMessage(String message, String title) {
        if (gui != null) {
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE));
        }
    }

    public void launchGUI() {
        this.gui = new ChessGUI(this);
        gui.showLoginScreen();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main app = Main.getInstance();
            app.launchGUI();
        });
    }
}