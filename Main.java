import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.FileWriter;
import java.util.*;

public class Main {
    // Singleton instance
    private static Main instance;

    private List<User> users = new ArrayList<>();
    private Map<Integer, Game> allGames = new HashMap<>();
    private User currentUser = null;
    private Scanner scanner = new Scanner(System.in);
    private int nextGameId = 1;
    private static final String ACCOUNTS_FILE = "accounts.json";
    private static final String GAMES_FILE = "games.json";

    // ChessGUI instance
    private ChessGUI gui;

    // Constructor privat pentru Singleton
    private Main() {
        read();
    }

    // Singleton getInstance
    public static Main getInstance() {
        if (instance == null) {
            instance = new Main();
        }
        return instance;
    }

    // Getter pentru GUI
    public ChessGUI getGUI() {
        return gui;
    }

    // Getters pentru GUI
    public User getCurrentUser() {
        return currentUser;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public Map<Integer, Game> getAllGames() {
        return new HashMap<>(allGames);
    }

    // Metode pentru GUI
    public void logout() {
        currentUser = null;
        write();
    }

    public Game createNewGame(String playerName, Colors playerColor) {
        Colors computerColor = (playerColor == Colors.WHITE) ? Colors.BLACK : Colors.WHITE;
        Player humanPlayer = new Player(playerName, playerColor);
        Player computerPlayer = new Player("Computer", computerColor);

        Game game = new Game(nextGameId, humanPlayer, computerPlayer);
        game.start();

        allGames.put(nextGameId, game);
        currentUser.addGame(game);
        nextGameId++;

        write();
        return game;
    }

    public void saveGame(Game game) {
        write();
    }

    public void resignGame(Game game) {
        Player humanPlayer = getHumanPlayer(game);
        int capturedPoints = humanPlayer.getPoints();
        int penalty = -150;
        int totalPoints = capturedPoints + penalty;

        currentUser.updatePoints(totalPoints);
        currentUser.removeGame(game);
        allGames.remove(game.getId());

        write();
    }

    public void endGame(Game game, boolean humanWins) {
        Player humanPlayer = getHumanPlayer(game);

        if (humanWins) {
            int capturedPoints = humanPlayer.getPoints();
            int bonus = 300;
            int totalPoints = capturedPoints + bonus;
            currentUser.updatePoints(totalPoints);
        } else {
            int capturedPoints = humanPlayer.getPoints();
            int penalty = -300;
            int totalPoints = capturedPoints + penalty;
            currentUser.updatePoints(totalPoints);
        }

        currentUser.removeGame(game);
        allGames.remove(game.getId());
        write();
    }

    private Player getHumanPlayer(Game game) {
        return (game.getPlayer1().getName().equals("Computer")) ?
                game.getPlayer2() : game.getPlayer1();
    }

    // Metode existente (modificate ușor pentru GUI)
    public void read() {
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
                    allGames.put(gameId, game);
                    if (gameId >= nextGameId) {
                        nextGameId = gameId + 1;
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
            System.out.println("Load error: " + e.getMessage());
        }
    }

    public void write() {
        try {
            for (User user : users) {
                List<Integer> gameIds = new ArrayList<>();
                for (Game game : user.getActiveGames()) {
                    gameIds.add(game.getId());
                }
                user.setGameIds(gameIds);
            }

            JSONArray accountsArray = new JSONArray();
            for (User user : users) {
                JSONObject obj = new JSONObject();
                obj.put("email", user.getEmail());
                obj.put("password", user.getPassword());
                obj.put("points", user.getPoints());
                JSONArray gamesArray = new JSONArray();
                for (Integer gameId : user.getGameIds()) {
                    gamesArray.add(gameId);
                }
                obj.put("games", gamesArray);
                accountsArray.add(obj);
            }

            try (FileWriter file = new FileWriter(ACCOUNTS_FILE)) {
                file.write(accountsArray.toJSONString());
            }

            JSONArray gamesArray = new JSONArray();
            for (Map.Entry<Integer, Game> entry : allGames.entrySet()) {
                Game game = entry.getValue();
                JSONObject gameObj = new JSONObject();
                gameObj.put("id", game.getId());

                JSONArray playersArray = new JSONArray();
                JSONObject player1 = new JSONObject();
                player1.put("email", game.getPlayer1().getName());
                player1.put("color", game.getPlayer1().getColor().toString());

                JSONObject player2 = new JSONObject();
                player2.put("email", game.getPlayer2().getName());
                player2.put("color", game.getPlayer2().getColor().toString());

                playersArray.add(player1);
                playersArray.add(player2);
                gameObj.put("players", playersArray);

                gameObj.put("currentPlayerColor", game.getCurrentPlayerColor());

                JSONArray boardArray = new JSONArray();
                if (game.getBoard() != null) {
                    List<ChessPair<Position, Piece>> pieces = game.getBoard().getAllPieces();
                    for (ChessPair<Position, Piece> pair : pieces) {
                        JSONObject pieceObj = new JSONObject();
                        pieceObj.put("type", String.valueOf(pair.getValue().getType()));
                        pieceObj.put("color", pair.getValue().getColor().toString());
                        pieceObj.put("position", pair.getKey().toString());
                        boardArray.add(pieceObj);
                    }
                }
                gameObj.put("board", boardArray);

                JSONArray movesArray = new JSONArray();
                for (Move move : game.getHistory()) {
                    JSONObject moveObj = new JSONObject();
                    moveObj.put("playerColor", move.getPlayerColor().toString());
                    moveObj.put("from", move.getFrom().toString());
                    moveObj.put("to", move.getTo().toString());
                    if (move.getCapturedPiece() != null) {
                        JSONObject captured = new JSONObject();
                        captured.put("type", String.valueOf(move.getCapturedPiece().getType()));
                        captured.put("color", move.getCapturedPiece().getColor().toString());
                        moveObj.put("captured", captured);
                    }
                    movesArray.add(moveObj);
                }
                gameObj.put("moves", movesArray);

                gamesArray.add(gameObj);
            }
            try (FileWriter file = new FileWriter(GAMES_FILE)) {
                file.write(gamesArray.toJSONString());
            }
        } catch (Exception e) {
            System.out.println("Save error: " + e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    public User login(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            if (gui != null) {
                JOptionPane.showMessageDialog(null, "Empty email");
            } else {
                System.out.println("Empty email");
            }
            return null;
        }
        if (!isValidEmail(email)) {
            if (gui != null) {
                JOptionPane.showMessageDialog(null, "Invalid email format");
            } else {
                System.out.println("Invalid email format");
            }
            return null;
        }
        email = email.trim().toLowerCase();
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                if (user.checkPassword(password)) {
                    currentUser = user;
                    if (gui != null) {
                        JOptionPane.showMessageDialog(null, "Logged in as: " + user.getEmail());
                    } else {
                        System.out.println("Logged in as: " + user.getEmail());
                    }
                    return user;
                } else {
                    if (gui != null) {
                        JOptionPane.showMessageDialog(null, "Wrong password");
                    } else {
                        System.out.println("Wrong password");
                    }
                    return null;
                }
            }
        }
        if (gui != null) {
            JOptionPane.showMessageDialog(null, "Account does not exist");
        } else {
            System.out.println("Account does not exist");
        }
        return null;
    }

    public User newAccount(String email, String password, String confirmPassword) {
        if (email == null || email.trim().isEmpty()) {
            if (gui != null) {
                JOptionPane.showMessageDialog(null, "Empty email");
            } else {
                System.out.println("Empty email");
            }
            return null;
        }
        if (!isValidEmail(email)) {
            if (gui != null) {
                JOptionPane.showMessageDialog(null, "Invalid email format");
            } else {
                System.out.println("Invalid email format");
            }
            return null;
        }
        if (password == null || password.trim().isEmpty()) {
            if (gui != null) {
                JOptionPane.showMessageDialog(null, "Empty password");
            } else {
                System.out.println("Empty password");
            }
            return null;
        }
        if (!password.equals(confirmPassword)) {
            if (gui != null) {
                JOptionPane.showMessageDialog(null, "Passwords do not match");
            } else {
                System.out.println("Passwords do not match");
            }
            return null;
        }
        email = email.trim().toLowerCase();
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                if (gui != null) {
                    JOptionPane.showMessageDialog(null, "Email already used");
                } else {
                    System.out.println("Email already used");
                }
                return null;
            }
        }
        User newUser = new User(email, password);
        users.add(newUser);
        currentUser = newUser;
        if (gui != null) {
            JOptionPane.showMessageDialog(null, "Account created for: " + email);
        } else {
            System.out.println("Account created for: " + email);
        }
        write();
        return newUser;
    }

    public void run() {
        read();
        boolean programRunning = true;
        while (programRunning) {
            if (currentUser == null) {
                programRunning = showLoginMenu();
            } else {
                programRunning = showMainMenu();
            }
        }
        write();
        scanner.close();
        System.out.println("Goodbye!");
    }

    private boolean showLoginMenu() {
        System.out.println("Welcome to Chess!");
        System.out.println("1. Login");
        System.out.println("2. New account");
        System.out.println("3. Exit");
        System.out.print("Choose: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                System.out.print("Email: ");
                String email = scanner.nextLine();
                System.out.print("Password: ");
                String password = scanner.nextLine();
                login(email, password);
                break;
            case "2":
                System.out.print("Email: ");
                String newEmail = scanner.nextLine();
                System.out.print("Password: ");
                String newPassword = scanner.nextLine();
                System.out.print("Confirm password: ");
                String confirmPassword = scanner.nextLine();
                newAccount(newEmail, newPassword, confirmPassword);
                break;
            case "3":
                return false;
            default:
                System.out.println("Invalid option!");
        }
        return true;
    }

    private boolean showMainMenu() {
        System.out.println("User: " + currentUser.getEmail());
        System.out.println("Points: " + currentUser.getPoints());
        System.out.println("Active games: " + currentUser.getActiveGames().size());
        System.out.println("1. New game");
        System.out.println("2. My games");
        System.out.println("3. Logout");
        System.out.println("4. Exit program");
        System.out.print("Choose: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                startNewGame();
                break;
            case "2":
                showActiveGames();
                break;
            case "3":
                System.out.println("Logged out");
                currentUser = null;
                break;
            case "4":
                return false;
            default:
                System.out.println("Invalid option!");
        }
        return true;
    }

    private void startNewGame() {
        System.out.println("New Game");
        System.out.print("Player name: ");
        String playerName = scanner.nextLine().trim();
        if (playerName.isEmpty())
            playerName = currentUser.getEmail();
        Colors playerColor = selectColor();
        if (playerColor == null)
            return;
        Colors computerColor = (playerColor == Colors.WHITE) ? Colors.BLACK : Colors.WHITE;
        Player humanPlayer = new Player(playerName, playerColor);
        Player computerPlayer = new Player("Computer", computerColor);
        Game game = new Game(nextGameId, humanPlayer, computerPlayer);
        game.start();
        allGames.put(nextGameId, game);
        currentUser.addGame(game);
        System.out.println("Game created #" + nextGameId);
        System.out.println("White: " + game.getPlayer1().getName());
        System.out.println("Black: " + game.getPlayer2().getName());
        nextGameId++;
        playGame(game, humanPlayer, computerPlayer);
    }

    private Colors selectColor() {
        while (true) {
            System.out.print("Choose color: ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("W")) {
                System.out.println("You chose white");
                return Colors.WHITE;
            } else if (input.equals("B")) {
                System.out.println("You chose black");
                return Colors.BLACK;
            } else {
                System.out.println("Enter W or B");
            }
        }
    }

    private void playGame(Game game, Player human, Player computer) {
        boolean gameRunning = true;
        while (gameRunning) {
            Player currentPlayer = game.getCurrentPlayer();
            boolean isHumanTurn = (currentPlayer == human);
            displayGameHeader(game, human, computer, isHumanTurn);
            displayBoard(game.getBoard());
            if (isHumanTurn) {
                boolean moveCompleted = false;
                boolean continueGame = true;
                while (!moveCompleted && continueGame) {
                    int turnResult = handleHumanTurnMenu(game, human);
                    switch (turnResult) {
                        case 1:
                            moveCompleted = true;
                            if (game.checkForCheckMate()) {
                                endGame(game, true);
                                gameRunning = false;
                            } else if (game.checkForStalemate()) {
                                endStalemateGame(game);
                                gameRunning = false;
                            } else {
                                game.switchPlayer();
                            }
                            break;
                        case 0:
                            continueGame = false;
                            gameRunning = false;
                            saveAndExitGame(game);
                            break;
                        case -1:
                            displayGameHeader(game, human, computer, isHumanTurn);
                            displayBoard(game.getBoard());
                            break;
                    }
                }
            } else {
                handleComputerTurn(game, computer);

                if (game.checkForCheckMate()) {
                    endGame(game, false);
                    gameRunning = false;
                } else if (game.checkForStalemate()) {
                    endStalemateGame(game);
                    gameRunning = false;
                } else {
                    game.switchPlayer();
                }
            }
        }
    }

    private void saveAndExitGame(Game game) {
        System.out.println("Game #" + game.getId() + " saved. You can resume it later.");
        write();
    }

    private void displayGameHeader(Game game, Player human, Player computer, boolean humanTurn) {
        System.out.println("Game #" + game.getId());
        System.out.println(human.getName() + " (" + human.getColor() + ")");
        System.out.println(computer.getName() + " (" + computer.getColor() + ")");
        System.out.println("Turn: " + (humanTurn ? "Your turn" : "Computer"));
        System.out.println("Moves played: " + game.getHistory().size());
    }

    private int handleHumanTurnMenu(Game game, Player player) {

        System.out.println("1. See possible moves");
        System.out.println("2. Enter move");
        System.out.println("3. Give Up");
        System.out.println("4. Save and quit");
        System.out.print("Choose: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                selectAndShowMoves(game, player);
                return -1;
            case "2":
                return attemptHumanMove(game, player);
            case "3":
                resignGame(game, player);
                return 0;
            case "4":
                System.out.println("Game saved.");
                return 0;
            default:
                System.out.println("Invalid command!");
                return -1;
        }
    }

    private int attemptHumanMove(Game game, Player player) {
        System.out.print("Enter move: ");
        String input = scanner.nextLine().trim().toUpperCase();

        if (input.equals("0")) {
            return -1;
        }

        try {
            if (!input.matches("[A-H][1-8]-[A-H][1-8]")) {
                throw new InvalidMoveException("Invalid format");
            }

            String[] parts = input.split("-");
            Position from = parsePosition(parts[0]);
            Position to = parsePosition(parts[1]);

            if (from.equals(to)) {
                throw new InvalidMoveException("Same positions");
            }

            Piece piece = game.getBoard().getPieceAt(from);
            if (piece == null) {
                throw new InvalidMoveException("No piece at " + from);
            }

            if (piece.getColor() != player.getColor()) {
                throw new InvalidMoveException("Not your piece");
            }

            player.makeMove(from, to, game.getBoard());
            game.addMove(player, from, to);

            System.out.println("Move: " + input);

            return 1;

        } catch (InvalidMoveException e) {
            System.out.println("Error: " + e.getMessage());
            return -1;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return -1;
        }
    }

    private void selectAndShowMoves(Game game, Player player) {
        System.out.print("Piece position: ");
        String input = scanner.nextLine().trim().toUpperCase();

        try {
            Position pos = parsePosition(input);
            Piece piece = game.getBoard().getPieceAt(pos);

            if (piece == null) {
                System.out.println("Empty square");
                return;
            }

            if (piece.getColor() != player.getColor()) {
                System.out.println("Opponent piece");
                return;
            }

            System.out.println(piece.getType() + " (" + piece.getColor() + ") at " + pos);
            List<Position> moves = piece.getPossibleMoves(game.getBoard());

            if (moves.isEmpty()) {
                System.out.println("No moves");
            } else {
                System.out.println("Possible moves (" + moves.size() + "):");
                for (int i = 0; i < moves.size(); i++) {
                    System.out.print(moves.get(i) + " ");
                    if ((i + 1) % 8 == 0)
                        System.out.println();
                }
                System.out.println();
            }

        } catch (Exception e) {
            System.out.println("Invalid position");
        }
    }

    private void resignGame(Game game, Player player) {
        System.out.println("You gave up!");

        int capturedPoints = player.getPoints();
        int penalty = -150;
        int totalPoints = capturedPoints + penalty;

        currentUser.updatePoints(totalPoints);
        currentUser.removeGame(game);
        allGames.remove(game.getId());

        System.out.println("Captures: " + capturedPoints + " | Penalty: " + penalty + " | Total: " + totalPoints);
        write();
    }

    private void endStalemateGame(Game game) {
        System.out.println("Draw by stalemate!");

        Player humanPlayer = (game.getPlayer1().getName().equals("Computer")) ? game.getPlayer2() : game.getPlayer1();
        int capturedPoints = humanPlayer.getPoints();

        currentUser.updatePoints(capturedPoints);
        System.out.println("+" + capturedPoints + " points from captured pieces");

        currentUser.removeGame(game);
        allGames.remove(game.getId());
        write();
    }

    private void handleComputerTurn(Game game, Player computer) {
        System.out.println("Computer thinking...");

        try {
            Board board = game.getBoard();
            List<Position> computerPieces = new ArrayList<>();

            for (char x = 'A'; x <= 'H'; x++) {
                for (int y = 1; y <= 8; y++) {
                    Position pos = new Position(x, y);
                    Piece piece = board.getPieceAt(pos);
                    if (piece != null && piece.getColor() == computer.getColor()) {
                        computerPieces.add(pos);
                    }
                }
            }

            if (computerPieces.isEmpty()) {
                System.out.println("Computer has no pieces!");
                return;
            }

            Random rand = new Random();
            int maxAttempts = 50;

            for (int attempt = 0; attempt < maxAttempts; attempt++) {
                Position from = computerPieces.get(rand.nextInt(computerPieces.size()));
                Piece piece = board.getPieceAt(from);

                if (piece == null)
                    continue;

                List<Position> moves = piece.getPossibleMoves(board);
                if (moves.isEmpty())
                    continue;

                Position to = moves.get(rand.nextInt(moves.size()));

                try {
                    computer.makeMove(from, to, board);
                    game.addMove(computer, from, to);

                    System.out.println("Computer: " + from + "-" + to);
                    return;

                } catch (InvalidMoveException e) {
                    continue;
                }
            }

            System.out.println("Computer cannot move");

        } catch (Exception e) {
            System.out.println("Computer error: " + e.getMessage());
        }
    }


    private void showActiveGames() {
        List<Game> games = currentUser.getActiveGames();
        if (games.isEmpty()) {
            System.out.println("No games in progress");
            return;
        }
        System.out.println("Active games");
        for (int i = 0; i < games.size(); i++) {
            Game game = games.get(i);
            System.out.println((i + 1) + ". Game #" + game.getId());
            System.out.println("   " + game.getPlayer1().getName() + " (" + game.getPlayer1().getColor() + ")");
            System.out.println("   vs " + game.getPlayer2().getName() + " (" + game.getPlayer2().getColor() + ")");
            System.out.println("   Turn: " + game.getCurrentPlayer().getName());
            System.out.println("   Moves: " + game.getHistory().size());
            System.out.println();
        }
        System.out.print("Game number to continue: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0)
                return;
            if (choice > 0 && choice <= games.size()) {
                continueGame(games.get(choice - 1));
            } else {
                System.out.println("Invalid number");
            }
        } catch (NumberFormatException e) {
            System.out.println("Enter a number");
        }
    }

    private void continueGame(Game game) {
        System.out.println("\n=== CONTINUE GAME ===");
        System.out.println("Game #" + game.getId());
        game.resume();

        Player humanPlayer = (game.getPlayer1().getName().equals("Computer")) ? game.getPlayer2() : game.getPlayer1();
        Player computerPlayer = (humanPlayer == game.getPlayer1()) ? game.getPlayer2() : game.getPlayer1();

        System.out.println("Human: " + humanPlayer.getName() + " (" + humanPlayer.getColor() + ")");
        System.out.println("Computer: " + computerPlayer.getName() + " (" + computerPlayer.getColor() + ")");

        playGame(game, humanPlayer, computerPlayer);
    }

    private Position parsePosition(String input) throws Exception {
        if (input.length() != 2) {
            throw new Exception("Invalid position");
        }
        char x = input.charAt(0);
        int y = Character.getNumericValue(input.charAt(1));
        if (x < 'A' || x > 'H' || y < 1 || y > 8) {
            throw new Exception("Position outside board");
        }
        return new Position(x, y);
    }

    private void displayBoard(Board board) {
        System.out.println("   ----------------------------------------");

        for (int y = 8; y >= 1; y--) {
            System.out.printf(" %d |", y);
            for (char x = 'A'; x <= 'H'; x++) {
                Position pos = new Position(x, y);
                Piece piece = board.getPieceAt(pos);
                if (piece == null) {
                    System.out.print(" ... ");
                } else {
                    char type = piece.getType();
                    String color = piece.getColor() == Colors.WHITE ? "W" : "B";
                    System.out.print(" " + type + "-" + color + " ");
                }
            }
            System.out.println("|");
        }

        System.out.println("   ----------------------------------------");
        System.out.println("       A    B    C    D    E    F    G    H");
    }

    // Metoda principală pentru GUI
    public void launchGUI() {
        this.gui = new ChessGUI(this);
        gui.showLoginScreen();
    }

    public static void main(String[] args) {
        Main app = Main.getInstance();

        // Pentru testare, porniți GUI (cerința temei)
        SwingUtilities.invokeLater(() -> {
            app.launchGUI();
        });

        // Pentru debugging, puteți comenta linia de mai sus
        // și decomenta linia de mai jos pentru CLI
        // app.run();
    }
}