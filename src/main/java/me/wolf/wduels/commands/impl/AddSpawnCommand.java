package me.wolf.wduels.commands.impl;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.arena.Arena;
import me.wolf.wduels.commands.SubCommand;
import me.wolf.wduels.constants.Messages;
import me.wolf.wduels.file.YamlConfig;
import me.wolf.wduels.utils.Utils;
import org.bukkit.entity.Player;

public class AddSpawnCommand extends SubCommand {
    @Override
    protected String getCommandName() {
        return "addspawn";
    }

    @Override
    protected String getUsage() {
        return "&a/duels addspawn <arena>";
    }

    @Override
    protected String getDescription() {
        return "&7Adds a player spawn to the arena";
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
            player.sendMessage(Messages.ARENA_NOT_FOUND);
            return;
        }
        final Arena arena = plugin.getArenaManager().getArenaByName(arenaName);
        final YamlConfig arenaYml = plugin.getFileManager().getArenasConfigFile();

        // adding a spawn to an arena, if the arena already has 2 spawns, replace the last one with the current location of the player
        if (canAddSpawn(arena)) {
            arenaYml.getConfig().set("arenas." + arenaName + ".spawns." + arena.getSpawns().size() + ".spawn", Utils.locationToString(player.getLocation()));
            player.sendMessage(Messages.ADDED_SPAWN.replace("{arena}", arenaName));
        } else {
            arena.getSpawns().remove(1); // last index
            arenaYml.getConfig().set("arenas." + arenaName + ".spawns." + arena.getSpawns().size() + ".spawn", Utils.locationToString(player.getLocation()));
            player.sendMessage(Utils.colorize("&cYou are creating too many spawnpoints for the size of this arena!\n" +
                    "Your last set spawn point has been replaced by this one."));
        }
        arena.addSpawn(player.getLocation());
        arenaYml.saveConfig();
    }

    private boolean canAddSpawn(final Arena arena) {
        return 2 > arena.getSpawns().size();
    }
}
