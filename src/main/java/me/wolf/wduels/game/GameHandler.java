package me.wolf.wduels.game;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.arena.ArenaState;
import me.wolf.wduels.player.PlayerState;
import org.bukkit.Bukkit;

public class GameHandler {

    // This class handles the game states and the events that need to happen in each state

    private final DuelsPlugin plugin;

    public GameHandler(final DuelsPlugin plugin) {
        this.plugin = plugin;
    }

    private GameState gameState;

    public void setGameState(Game game, GameState gameState) {
        this.gameState = gameState;
        switch (gameState) {
            case QUEUE:
                game.setGameState(GameState.QUEUE);
                game.getQueue().forEach(queueMember -> queueMember.setState(PlayerState.IN_QUEUE));
                game.getArena().setState(ArenaState.QUEUE);
                break;
            case ONGOING:
                game.setGameState(GameState.ONGOING);
                game.getQueue().forEach(queueMember -> {
                    queueMember.setState(PlayerState.IN_GAME);
                    plugin.getGameManager().applyScoreboard(queueMember, game);
                });
                game.getArena().setState(ArenaState.ONGOING);
                plugin.getGameManager().startGame(game);
                break;
            case END:
                game.setGameState(GameState.END);
                game.getQueue().forEach(queueMember -> queueMember.setState(PlayerState.IN_GAME));
                game.getArena().setState(ArenaState.END);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    game.getArena().setState(ArenaState.PREQUEUE);
                    game.getArena().resetTimer();
                    plugin.getGameManager().cleanUpGame(game);
                }, 200L);
                Bukkit.getLogger().info("The game instance " + game + " has ended!");
                break;
        }
    }
}
