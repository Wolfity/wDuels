package me.wolf.wduels.managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.player.DuelPlayer;
import me.wolf.wduels.sql.Query;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public class SQLiteManager {

    private final DuelsPlugin plugin;
    private HikariDataSource hikari;

    // manager that handles any SQLite related stuff, saving data, loading data, creating data, etc...

    public SQLiteManager(final DuelsPlugin plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        final HikariConfig config = new HikariConfig();
        config.setConnectionTestQuery("SELECT 1");
        config.setPoolName("wDuels Pool");
        config.setDriverClassName("org.sqlite.JDBC");
        final File file = new File(plugin.getDataFolder(), "database.db");
        config.setJdbcUrl("jdbc:sqlite:" + file.getAbsolutePath().replace("\\", "/"));

        hikari = new HikariDataSource(config);

        createTablesIfNotExist();
    }

    public void createPlayerData(final UUID uuid, final String playerName) { // if the player exists, load their data, else create new data
        if (doesPlayerExist(uuid)) {
            loadData(uuid);
        } else {

            try (final Connection connection = hikari.getConnection();
                 final PreparedStatement ps = connection.prepareStatement(Query.CREATE_PLAYERDATA)) {

                ps.setString(1, uuid.toString());
                ps.setString(2, playerName);
                ps.setInt(3, 0); // wins
                ps.setInt(4, 0); // kills
                ps.setString(5, "default"); // killeffect
                ps.setString(6, "default"); //wineffect

                ps.executeUpdate();

            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        if (hikari != null)
            hikari.close();
    }

    private void loadData(final UUID uuid) { // load data of an existing player

        this.setWins(uuid, this.getWins(uuid));
        this.setKills(uuid, this.getKills(uuid));
        this.setWinEffect(uuid, this.getWinEffect(uuid));
        this.setKillEffect(uuid, this.getKillEffect(uuid));

        plugin.getPlayerManager().addDuelPlayer(
                uuid,
                getWins(uuid),
                getKills(uuid),
                plugin.getKillEffectManager().getKillEffectByName(getKillEffect(uuid)),
                plugin.getWinEffectManager().getWinEffectByName(getWinEffect(uuid)));

    }

    public boolean doesPlayerExist(final UUID uuid) { // checking if a specific player exisits in the database
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.GET_PLAYERDATA)) {
            ps.setString(1, uuid.toString());
            final ResultSet results = ps.executeQuery();

            return results.next();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setPlayerName(final UUID uuid, final String playerName) { // setting the playername in the db
        if (!doesPlayerExist(uuid)) return;

        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.SET_PLAYER_NAME)) {

            ps.setString(1, playerName);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (final SQLException e) {
            e.printStackTrace();
        }


    }

    private void createTablesIfNotExist() { // if the table doesn't exist, create it
        try (final Connection connection = hikari.getConnection();
             final Statement statement = connection.createStatement()) {

            statement.executeUpdate(Query.CREATE_TABLE);

        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    // methods below are for updating/getting stats, kill and win effects
    public void setWins(final UUID uuid, final int wins) {
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.SET_WINS)) {

            ps.setInt(1, wins);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public int getWins(final UUID uuid) {

        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.GET_PLAYERDATA)) {

            ps.setString(1, uuid.toString());

            final ResultSet results = ps.executeQuery();

            return results.getInt("wins");
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void setKills(final UUID uuid, final int wins) {
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.SET_KILLS)) {

            ps.setInt(1, wins);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public int getKills(final UUID uuid) {

        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.GET_PLAYERDATA)) {

            ps.setString(1, uuid.toString());

            final ResultSet results = ps.executeQuery();

            return results.getInt("kills");
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void setKillEffect(final UUID uuid, final String killEffect) {
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.SET_KILLEFFECT)) {

            ps.setString(1, killEffect);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public void setWinEffect(final UUID uuid, final String winEffect) {
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.SET_WINEFFECT)) {

            ps.setString(1, winEffect);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }


    public String getWinEffect(final UUID uuid) {

        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.GET_PLAYERDATA)) {

            ps.setString(1, uuid.toString());

            final ResultSet results = ps.executeQuery();

            return results.getString("wineffect");
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getKillEffect(final UUID uuid) {

        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.GET_PLAYERDATA)) {

            ps.setString(1, uuid.toString());

            final ResultSet results = ps.executeQuery();

            return results.getString("killeffect");
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void saveData(final DuelPlayer duelPlayer) { // saving all data of the specified player
        this.setPlayerName(duelPlayer.getUuid(), duelPlayer.getName());
        this.setWins(duelPlayer.getUuid(), duelPlayer.getWins());
        this.setKills(duelPlayer.getUuid(), duelPlayer.getKills());
        this.setWinEffect(duelPlayer.getUuid(), duelPlayer.getWinEffect().getName());
        this.setKillEffect(duelPlayer.getUuid(), duelPlayer.getKillEffect().getName());

    }

}
