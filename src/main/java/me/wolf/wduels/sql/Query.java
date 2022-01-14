package me.wolf.wduels.sql;

public final class Query {

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "players (uuid VARCHAR(64) " +
            "NOT NULL, " +
            "name VARCHAR(16), " +
            "wins INT, " +
            "kills INT, " +
            "killeffect VARCHAR(16), " +
            "wineffect VARCHAR(16))";

    public final static String CREATE_PLAYERDATA = "INSERT INTO players (uuid, name, wins, kills, killeffect, wineffect) VALUES (?,?,?,?,?,?)";
    public final static String SET_PLAYER_NAME = "UPDATE players SET name = ? WHERE uuid = ?";
    public final static String SET_WINS = "UPDATE players SET wins = ? WHERE uuid = ?";
    public final static String SET_KILLS = "UPDATE players SET kills = ? WHERE uuid = ?";
    public final static String SET_WINEFFECT = "UPDATE players SET wineffect = ? WHERE uuid = ?";
    public final static String SET_KILLEFFECT = "UPDATE players SET killeffect = ? WHERE uuid = ?";
    public final static String GET_PLAYERDATA = "SELECT * FROM players WHERE uuid = ?";

    private Query() {
    }

}


