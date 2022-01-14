package me.wolf.wduels.commands.impl;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.commands.SubCommand;
import me.wolf.wduels.constants.Messages;
import me.wolf.wduels.utils.Utils;
import org.bukkit.entity.Player;

public class SetSpawnCommand extends SubCommand {
    @Override
    protected String getCommandName() {
        return "setspawn";
    }

    @Override
    protected String getUsage() {
        return "&a/duels setspawn";
    }

    @Override
    protected String getDescription() {
        return "&7Set the spawn";
    }

    @Override
    protected void executeCommand(Player player, String[] args, DuelsPlugin plugin) {
        if (!isAdmin(player)) {
            player.sendMessage(Utils.colorize("&cNo Permission!"));
            return;
        }

        if (args.length != 1) {
            player.sendMessage(getUsage());
            return;
        }

        plugin.getConfig().set("spawn", Utils.locationToString(player.getLocation()));
        plugin.saveConfig();
        player.sendMessage(Messages.SET_LOBBY_SPAWN);

    }
}
