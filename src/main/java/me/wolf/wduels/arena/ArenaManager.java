package me.wolf.wduels.arena;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.file.YamlConfig;
import me.wolf.wduels.player.DuelPlayer;
import me.wolf.wduels.team.Team;
import me.wolf.wduels.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

public class ArenaManager {

    private final DuelsPlugin plugin;

    private final Set<Arena> arenas = new HashSet<>();

    public ArenaManager(DuelsPlugin plugin) {
        this.plugin = plugin;
    }

    public void createArena(final String name, YamlConfig cfg) { // creation of arenas and putting the appropriate vals in the config
        final Arena arena = new Arena(plugin, name);
        arenas.add(arena);

        cfg.getConfig().createSection("arenas." + name);
        cfg.getConfig().set("arenas." + name + ".timer", arena.getTimer());

        cfg.saveConfig();
    }

    public void deleteArena(final String name, YamlConfig cfg) {
        arenas.remove(getArenaByName(name));
        cfg.getConfig().set("arenas." + name, null);
        cfg.saveConfig();
    }

    public void loadArenas(YamlConfig cfg) { // load arena names, spawns etc. Cache the arena objects
        try {
            for (String ar : cfg.getConfig().getConfigurationSection("arenas").getKeys(false)) {
                final Arena arena = new Arena(plugin, ar);

                arena.setTimer(cfg.getConfig().getInt("arenas." + ar + ".timer"));

                for (int i = 0; i < 2; i++) {
                    arena.addSpawn(Utils.stringToLoc(cfg.getConfig().getString("arenas." + ar + ".spawns." + i + ".spawn").split(" ")));
                }
                arenas.add(arena);
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
            Bukkit.getServer().getLogger().info(Utils.colorize("&4No Arenas were loaded!"));
        }
    }

    public Arena getFreeArena() { // get an arena that is not occupied
        return arenas.stream().filter(arena -> arena.getState() == ArenaState.PREQUEUE).findFirst().orElse(null);
    }

    public Arena getArenaByName(final String name) { // get an arena by passing in the string name
        return arenas.stream().filter(arena -> arena.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Arena getArenaByPlayer(final DuelPlayer player) { // get an arena by passing in a player
        for (Arena arena : arenas) {
            for (Team team : arena.getTeams()) {
                if (team.getMembers().contains(player)) {
                    return arena;
                }
            }
        }
        return null;
    }

    public boolean doesArenaExist(final String arenaName) { // checking if a specific arena exists
        for (Arena a : arenas) {
            if (a.getName().equalsIgnoreCase(arenaName)) {
                return true;
            }
        }
        return false;
    }

    public Set<Arena> getArenas() {
        return arenas;
    }
}
