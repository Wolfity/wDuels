package me.wolf.wduels.game.games;

import me.wolf.wduels.arena.Arena;
import me.wolf.wduels.game.Game;
import me.wolf.wduels.game.GameType;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class ModifiableGame extends Game { // for games such as UHC, where you can modify the arena

    private final List<Location> placedBlocks;


    public ModifiableGame(Arena arena, GameType gameType) {
        super(arena, gameType);
        this.placedBlocks = new ArrayList<>();
    }

    @Override
    public void setPrivate(boolean aPrivate) {
        super.setPrivate(true);
    }

    public List<Location> getPlacedBlocks() {
        return placedBlocks;
    }

    @Override
    public boolean isModifiable() {
        return true;
    }
}
