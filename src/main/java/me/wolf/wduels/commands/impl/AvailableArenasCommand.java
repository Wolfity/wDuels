package me.wolf.wduels.commands.impl;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.arena.Arena;
import me.wolf.wduels.arena.ArenaState;
import me.wolf.wduels.commands.SubCommand;
import me.wolf.wduels.utils.Utils;
import org.bukkit.entity.Player;

public class AvailableArenasCommand extends SubCommand {
    @Override
    protected String getCommandName() {
        return "available";
    }

    @Override
    protected String getUsage() {
        return "&a/duels available";
    }

    @Override
    protected String getDescription() {
        return "&7Show a list of available arenas";
    }

    @Override
    protected void executeCommand(Player player, String[] args, DuelsPlugin plugin) {
        if (args.length != 1) {
            player.sendMessage(Utils.colorize(getUsage()));
            return;
        }

        final StringBuilder result = new StringBuilder();
        result.append(Utils.colorize("&aAvailable Arenas\n"));
        for (final Arena available : plugin.getArenaManager().getArenas()) {
            if (available.getState() == ArenaState.PREQUEUE) {
                result.append("&a- &2").append(available.getName()).append("\n");
            }
        }
        player.sendMessage(Utils.colorize(result.toString()));

    }
}
