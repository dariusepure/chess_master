import java.util.ArrayList;
import java.util.List;

public class User {
    private String email;
    private String password;
    private List<Game> games;
    private int points;
    private List<Integer> gameIds;

    public User() {
        this.email = "";
        this.password = "";
        this.games = new ArrayList<Game>();
        this.points = 0;
        this.gameIds = new ArrayList<Integer>();
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.games = new ArrayList<Game>();
        this.points = 0;
        this.gameIds = new ArrayList<Integer>();
    }

    public User(String email, String password, int points, List<Integer> gameIds) {
        this.email = email;
        this.password = password;
        this.points = points;
        this.games = new ArrayList<Game>();
        if (gameIds != null) {
            this.gameIds = new ArrayList<Integer>();
            for (Integer id : gameIds) {
                if (id != null) {
                    this.gameIds.add(id);
                }
            }
        } else {
            this.gameIds = new ArrayList<Integer>();
        }
    }

    public void addGame(Game game) {
        if (game == null) return;
        for (Game g : games) {
            if (g.getId() == game.getId()) return;
        }
        games.add(game);
        if (!gameIds.contains(game.getId())) {
            gameIds.add(game.getId());
        }
    }

    public void removeGame(Game game) {
        if (game == null) return;
        Game toRemove = null;
        for (Game g : games) {
            if (g.getId() == game.getId()) {
                toRemove = g;
                break;
            }
        }
        if (toRemove != null) {
            games.remove(toRemove);
            gameIds.remove(Integer.valueOf(game.getId()));
        }
    }

    public List<Game> getActiveGames() {
        return new ArrayList<Game>(games);
    }

    public void setGames(List<Game> games) {
        if (games != null) {
            this.games = new ArrayList<Game>(games);
        } else {
            this.games = new ArrayList<Game>();
        }
        this.gameIds.clear();
        for (Game game : this.games) {
            this.gameIds.add(game.getId());
        }
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void updatePoints(int delta) {
        this.points += delta;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public List<Integer> getGameIds() {
        return new ArrayList<Integer>(gameIds);
    }

    public void setGameIds(List<Integer> gameIds) {
        if (gameIds != null) {
            this.gameIds = new ArrayList<Integer>();
            for (Integer id : gameIds) {
                if (id != null) {
                    this.gameIds.add(id);
                }
            }
        } else {
            this.gameIds = new ArrayList<Integer>();
        }
    }

    @Override
    public String toString() {
        return "User{email='" + email + "', points=" + points + ", games=" + games.size() + "}";
    }
}