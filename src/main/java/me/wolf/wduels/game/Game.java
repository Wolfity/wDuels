package me.wolf.wduels.game;

import me.wolf.wduels.arena.Arena;
import me.wolf.wduels.player.DuelPlayer;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Game {


    private final Set<DuelPlayer> queue;
    private final Arena arena;
    private GameType gameType;
    private GameState gameState;
    private boolean isModifiable;
    private boolean isPrivate;

    public Game(final Arena arena, final GameType gameType) {
        this.arena = arena;
        this.gameType = gameType;
        this.queue = new HashSet<>();
        this.gameState = GameState.QUEUE;
        this.isModifiable = false;
        this.isPrivate = false;
    }


    public Set<DuelPlayer> getQueue() {
        return queue;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Arena getArena() {
        return arena;
    }

    public boolean isModifiable() {
        return isModifiable;
    }

    public void setModifiable(boolean modifiable) {
        isModifiable = modifiable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return gameType == game.gameType && arena.equals(game.arena);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameType, arena);
    }
}
