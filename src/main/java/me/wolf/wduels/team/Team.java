package me.wolf.wduels.team;

import me.wolf.wduels.player.DuelPlayer;

import java.util.Objects;
import java.util.Set;

public class Team {

    private final String name;
    private final Set<DuelPlayer> members;


    public Team(final String name, Set<DuelPlayer> members) {
        this.name = name;
        this.members = members;
    }

    public Set<DuelPlayer> getMembers() {
        return members;
    }

    public void addMember(final DuelPlayer player) {
        members.add(player);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return name.equals(team.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
