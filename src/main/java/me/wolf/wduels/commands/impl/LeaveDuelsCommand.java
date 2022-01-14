package me.wolf.wduels.commands.impl;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.commands.SubCommand;
import me.wolf.wduels.constants.Messages;
import me.wolf.wduels.player.DuelPlayer;
import me.wolf.wduels.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LeaveDuelsCommand extends SubCommand {
    @Override
    protected String getCommandName() {
        return "leave";
    }

    @Override
    protected String getUsage() {
        return "&a/duels leave";
    }

    @Override
    protected String getDescription() {
        return "&7Leave duels";
    }

    @Override
    protected void executeCommand(Player player, String[] args, DuelsPlugin plugin) {
        final DuelPlayer duelPlayer = plugin.getPlayerManager().getDuelPlayer(player.getUniqueId());

        if (args.length != 1) {
            player.sendMessage(Utils.colorize(getUsage()));
            return;
        }

        if (duelPlayer == null) {
            player.sendMessage(Messages.NOT_IN_DUELS);
            return;
        }

        plugin.getPlayerManager().removeDuelPlayer(player.getUniqueId());
        player.getInventory().clear();
        player.sendMessage(Messages.LEFT_DUELS);
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }
}
