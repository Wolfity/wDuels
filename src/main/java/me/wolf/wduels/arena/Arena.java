package me.wolf.wduels.arena;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.team.Team;
import org.bukkit.Location;

import java.util.*;

public class Arena {

    private final DuelsPlugin plugin;

    private final String name;
    private final Set<Team> teams;
    private final List<Location> spawns;
    private int timer;
    private ArenaState state;

    public Arena(final DuelsPlugin plugin, final String name) {
        this.plugin = plugin;
        this.name = name;
        this.timer = 180;
        this.state = ArenaState.PREQUEUE;
        this.teams = new HashSet<>();
        this.spawns = new ArrayList<>();

    }

    public Team getTeamByName(final String name) {
        for (final Team team : teams) {
            if (team.getName().equalsIgnoreCase(name)) {
                return team;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public Set<Team> getTeams() {
        return teams;
    }

    public void addTeam(Team team) {
        teams.add(team);
    }


    public List<Location> getSpawns() {
        return spawns;
    }

    public void addSpawn(final Location location) {
        spawns.add(location);
    }

    public ArenaState getState() {
        return state;
    }

    public void setState(ArenaState state) {
        this.state = state;
    }

    public void decrementTimer() {
        this.timer--;
    }

    public void resetTimer() {
        this.timer = plugin.getFileManager().getArenasConfigFile().getConfig().getInt("arenas." + name + ".timer");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arena arena = (Arena) o;
        return name.equals(arena.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
