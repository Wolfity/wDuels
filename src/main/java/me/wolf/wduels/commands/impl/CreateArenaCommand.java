package me.wolf.wduels.commands.impl;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.commands.SubCommand;
import me.wolf.wduels.constants.Messages;
import me.wolf.wduels.utils.Utils;
import org.bukkit.entity.Player;

public class CreateArenaCommand extends SubCommand {
    @Override
    protected String getCommandName() {
        return "createarena";
    }

    @Override
    protected String getUsage() {
        return "&a/duels createarena <arena>";
    }

    @Override
    protected String getDescription() {
        return "&7Create a new arena";
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

        if (plugin.getArenaManager().doesArenaExist(arenaName)) {
            player.sendMessage(Utils.colorize("&aThis arena already exists"));
            return;
        }
        plugin.getArenaManager().createArena(arenaName, plugin.getFileManager().getArenasConfigFile());
        player.sendMessage(Messages.ARENA_CREATED.replace("{arena}", arenaName));

    }
}
