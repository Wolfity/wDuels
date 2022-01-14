package me.wolf.wduels.commands;

import me.wolf.wduels.DuelsPlugin;
import org.bukkit.entity.Player;

public abstract class SubCommand {

    protected abstract String getCommandName();

    protected abstract String getUsage();

    protected abstract String getDescription();

    protected abstract void executeCommand(final Player player, final String[] args, final DuelsPlugin plugin);

    protected boolean isAdmin(final Player player) {
        return player.hasPermission("duels.admin");
    }

}
