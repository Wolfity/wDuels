package me.wolf.wduels.commands.impl;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.commands.SubCommand;
import me.wolf.wduels.constants.Messages;
import me.wolf.wduels.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class JoinDuelsCommand extends SubCommand {
    @Override
    protected String getCommandName() {
        return "join";
    }

    @Override
    protected String getUsage() {
        return "&a/duels join";
    }

    @Override
    protected String getDescription() {
        return "&7Join duels";
    }

    @Override
    protected void executeCommand(Player player, String[] args, DuelsPlugin plugin) {
        if (args.length != 1) {
            player.sendMessage(Utils.colorize(getUsage()));
            return;
        }
        if (plugin.getPlayerManager().getDuelPlayer(player.getUniqueId()) != null) {
            player.sendMessage(Utils.colorize("&cYou are already in duels!"));
            return;
        }

        joinDuels(player, plugin);

    }

    private void joinDuels(final Player player, final DuelsPlugin plugin) { // add a player to the duel gamemode, load their stats, kill/win effects, give inventory, etc

        // teleporting to the duels lobby
        if (plugin.getConfig().getString("spawn") != null) {
            player.teleport(Utils.stringToLoc(plugin.getConfig().getString("spawn").split(" ")));

            plugin.getPlayerManager().addDuelPlayer(player.getUniqueId());

            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            player.getInventory().clear();

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getSqLiteManager().createPlayerData(player.getUniqueId(), player.getName()));
            Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getScoreboard().lobbyScoreboard(player), 10L);

            plugin.getPlayerManager().getDuelPlayer(player.getUniqueId()).giveLobbyInv();

            player.sendMessage(Messages.JOINED_DUELS);
        } else player.sendMessage(Utils.colorize("&cNo Spawn was set, contact an administrator!"));


    }
}
