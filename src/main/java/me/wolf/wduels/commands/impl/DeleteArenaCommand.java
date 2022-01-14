package me.wolf.wduels.commands.impl;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.commands.SubCommand;
import me.wolf.wduels.constants.Messages;
import me.wolf.wduels.utils.Utils;
import org.bukkit.entity.Player;

public class DeleteArenaCommand extends SubCommand {
    @Override
    protected String getCommandName() {
        return "deletearena";
    }

    @Override
    protected String getUsage() {
        return "&a/duels deletearena <arena>";
    }

    @Override
    protected String getDescription() {
        return "&7Delete an arena";
    }

    @Override
    protected void executeCommand(Player player, String[] args, DuelsPlugin plugin) {
        if (!isAdmin(player)) {
            player.sendMessage(Utils.colorize("&cNo Permission!"));
            return;
        }
        if (args.length != 2) {
            player.sendMessage(Utils.colorize(getUsage()));
            return;
        }
        final String arenaName = args[1];
        if (plugin.getArenaManager().getArenaByName(arenaName) == null) {
            player.sendMessage(Messages.ARENA_NOT_FOUND.replace("{arena}", arenaName));
            return;
        }
        plugin.getArenaManager().deleteArena(arenaName, plugin.getFileManager().getArenasConfigFile());
        player.sendMessage(Messages.ARENA_DELETED.replace("{arena}", arenaName));
    }
}
