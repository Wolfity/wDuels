package me.wolf.wduels.game.games;

import me.wolf.wduels.arena.Arena;
import me.wolf.wduels.game.GameType;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class PrivateModifiableGame extends PrivateGame {

    private final List<Location> placedBlocks;

    public PrivateModifiableGame(Arena arena, GameType gameType) {
        super(arena, gameType);
        this.placedBlocks = new ArrayList<>();
    }

    @Override
    public boolean isPrivate() {
        return true;
    }

    @Override
    public boolean isModifiable() {
        return true;
    }

    public List<Location> getPlacedBlocks() {
        return placedBlocks;
    }
}
