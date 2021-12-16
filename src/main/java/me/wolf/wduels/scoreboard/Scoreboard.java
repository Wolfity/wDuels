package me.wolf.wduels.scoreboard;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.game.Game;
import me.wolf.wduels.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class Scoreboard {

    private final DuelsPlugin plugin;

    public Scoreboard(final DuelsPlugin plugin) {
        this.plugin = plugin;
    }

    public void lobbyScoreboard(final Player player) {

        final ScoreboardManager scoreboardManager = plugin.getServer().getScoreboardManager();
        org.bukkit.scoreboard.Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

        final Objective objective = scoreboard.registerNewObjective("duels", "duels");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(Utils.colorize("&a&lDuels Lobby"));

        final Team kills = scoreboard.registerNewTeam("kills");
        kills.addEntry(Utils.colorize("&7Kills: "));
        kills.setPrefix("");
        kills.setSuffix(Utils.colorize("&a " + plugin.getPlayerManager().getDuelPlayer(player.getUniqueId()).getKills()));
        objective.getScore(Utils.colorize("&7Kills: ")).setScore(1);

        final Team wins = scoreboard.registerNewTeam("wins");
        wins.addEntry(Utils.colorize("&7Wins: "));
        wins.setPrefix("");
        wins.setSuffix(Utils.colorize("&a" + plugin.getPlayerManager().getDuelPlayer(player.getUniqueId()).getWins()));
        objective.getScore(Utils.colorize("&7Wins: ")).setScore(2);

        player.setScoreboard(scoreboard);
    }

    public void gameScoreboard(final Player player, final Game game) {
        final String name = game.getArena().getName();

        final ScoreboardManager scoreboardManager = plugin.getServer().getScoreboardManager();
        org.bukkit.scoreboard.Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

        final Objective objective = scoreboard.registerNewObjective("duels", "duels");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(Utils.colorize("&2&lDuels Game"));

        final Team time = scoreboard.registerNewTeam("time");
        time.addEntry(Utils.colorize("&bTime: "));
        time.setPrefix("");
        time.setSuffix(Utils.colorize("&7" + game.getArena().getTimer()));
        objective.getScore(Utils.colorize("&bTime: ")).setScore(1);

        final Team empty1 = scoreboard.registerNewTeam("empty1");
        empty1.addEntry(" ");
        empty1.setPrefix("");
        empty1.setSuffix("");
        objective.getScore(" ").setScore(2);

        final Team map = scoreboard.registerNewTeam("map");
        map.addEntry(Utils.colorize("&bMap: &2"));
        map.setPrefix("");
        map.setSuffix(Utils.colorize(name));
        objective.getScore(Utils.colorize("&bMap: &2")).setScore(3);

        final Team empty2 = scoreboard.registerNewTeam("empty2");
        empty2.addEntry("  ");
        empty2.setPrefix("");
        empty2.setSuffix("");
        objective.getScore("  ").setScore(4);

        final Team gameType = scoreboard.registerNewTeam("gametype");
        gameType.addEntry(Utils.colorize("&7Game Mode: &a"));
        gameType.setPrefix("");
        gameType.setSuffix(Utils.colorize(game.getGameType().getIdentifier()));
        objective.getScore(Utils.colorize("&7Game Mode: &a")).setScore(5);


        player.setScoreboard(scoreboard);
    }


}
