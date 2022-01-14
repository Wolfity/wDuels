package me.wolf.wduels.managers;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.arena.Arena;
import me.wolf.wduels.constants.Messages;
import me.wolf.wduels.exception.InvalidSpawnsException;
import me.wolf.wduels.game.Game;
import me.wolf.wduels.game.GameState;
import me.wolf.wduels.game.GameType;
import me.wolf.wduels.game.games.ModifiableGame;
import me.wolf.wduels.game.games.PrivateGame;
import me.wolf.wduels.game.games.PrivateModifiableGame;
import me.wolf.wduels.player.DuelPlayer;
import me.wolf.wduels.player.PlayerState;
import me.wolf.wduels.team.Team;
import me.wolf.wduels.utils.ItemUtils;
import me.wolf.wduels.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class GameManager {

    private final DuelsPlugin plugin;
    private final Set<Game> games = new HashSet<>();

    public GameManager(final DuelsPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * @param arena     the arena we are using for this game object
     * @param gameType  the gametype used for this game object
     * @param isPrivate whether the game is a private game or not
     * @return a new Game object
     */
    public Game createGame(final Arena arena, final GameType gameType, final boolean isPrivate) {
        if (isPrivate) {
            if (gameType == GameType.UHC) {
                final PrivateModifiableGame game = new PrivateModifiableGame(arena, gameType);
                games.add(game);
                return game;
            } else {
                final PrivateGame game = new PrivateGame(arena, gameType);
                games.add(game);
                return game;
            }
        } else {
            if (gameType == GameType.UHC) {
                final ModifiableGame game = new ModifiableGame(arena, gameType);
                game.setModifiable(true);
                games.add(game);
                return game;
            } else {
                final Game game = new Game(arena, gameType);
                games.add(game);
                return game;
            }
        }

    }

    public Game getFreeGameByGameType(final GameType gameType) { // for the people in the queue, find a matching queue
        return games.stream().filter(g -> g.getGameType() == gameType).filter(g -> g.getGameState() == GameState.QUEUE).filter(game -> !game.isPrivate()).findFirst().orElse(null);
    }

    /**
     * @param duelPlayer checking if this player is in the queue
     * @return true if the player is in the queue, false if not
     */
    public boolean isPlayerInQueue(final DuelPlayer duelPlayer) { // check if a specific player is in a queue
        return games.stream().anyMatch(game -> game.getQueue().contains(duelPlayer));
    }

    public Game getGameByPlayer(final DuelPlayer player) { // getting a game by the player
        return games.stream().filter(game -> game.getQueue().contains(player)).findFirst().orElse(null);
    }

    /**
     * @param killed the killed player
     * @param game   the game a player was killed in
     */
    public void handleGameKill(final DuelPlayer killed, final Game game) { // if a kill was made, remove from the queue, send the game result and a msg
        plugin.getScoreboard().lobbyScoreboard(killed.getBukkitPlayer());
        killed.setHasLost(true);
        handleResult(game);
        game.getQueue().forEach(queueMember -> queueMember.sendMessage("&c" + killed.getName() + " &7was killed!"));
        removeFromQueue(game, killed);
    }

    /**
     * @param game method to clean up the arena/game after the game has ended
     */
    public void cleanUpGame(final Game game) { // deals with the end of the game, save data, teleport to lobby, and removing the last player from the queue
        teleportToLobby(game);
        if (game.isModifiable()) {
            if (!game.isPrivate()) {
                for (final Location loc : ((ModifiableGame) game).getPlacedBlocks()) {
                    loc.getBlock().setType(Material.AIR);
                }
            } else {
                for (final Location loc : ((PrivateModifiableGame) game).getPlacedBlocks()) {
                    loc.getBlock().setType(Material.AIR);
                }
            }
        }
        game.getQueue().forEach(queueMember -> {
            queueMember.getInventory().clear();
            if (!isDraw(game) && !game.isPrivate()) { // if the game ends in a draw, no kills/wins are added, so no need to save anything to the database
                // Also not saving stats in private games (/duels duel <user>) to prevent stat boosting ;)

                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getSqLiteManager().saveData(queueMember));
            }
            queueMember.setState(PlayerState.IN_LOBBY);
            plugin.getScoreboard().lobbyScoreboard(queueMember.getBukkitPlayer());
            queueMember.giveLobbyInv();
            queueMember.fillHunger();
            queueMember.clearEffects();
        });

        game.getQueue().clear();
        games.remove(game);

        Bukkit.getLogger().info("The game instance " + game + " has been ended! Arena is now available again");
    }

    /**
     * @param game method that deals with sending the users a message when the game is over
     */
    private void handleResult(final Game game) { // sending the game result to the player
        if (isDraw(game)) {
            game.getQueue().forEach(queueMember -> {
                queueMember.sendCenteredMessage(new String[]{
                        "&7-----------------------------------------------------",
                        "",
                        "&7The timer ran out - &6&lDraw",
                        "",
                        "&7-----------------------------------------------------"

                });

            });
        } else {
            game.getQueue().forEach(queueMember -> {
                final String result = isWinner(queueMember) ? "&a&lWon" : "&c&lLost";
                for (final String s : Messages.GAME_ENDED) {
                    queueMember.sendCenteredMessage(s
                            .replace("{result}", result));
                }
            });
        }
    }

    /**
     * @param game   the game a player left
     * @param player the player that left the game
     *               Method that deals with a player leaving the game, making the necessary checks
     */
    public void handleGameLeave(final Game game, final DuelPlayer player) { // if a user leaves a game, remove the user from the queue and end the game
        removeFromQueue(game, player);
        if (game.getQueue().size() < 2) { // there are less players in the game then there are required to be (2)
            plugin.getGameHandler().setGameState(game, GameState.END);
        }
    }

    /**
     * @param game   the game a played joined
     * @param player the player that joined the game
     *               Adding the player to the queue and performing the necessary checks
     */
    public void joinQueue(final Game game, final DuelPlayer player) { // add a user to the queue, apply scoreboard

        player.getInventory().setItem(8, ItemUtils.createItem(Material.BARRIER, "&cClick To Dequeue")); // give inventory
        final Set<DuelPlayer> teamMembers = new HashSet<>();
        teamMembers.add(player);
        game.getQueue().add(player);

        final Team team = new Team(player.getName(), teamMembers);
        game.getArena().addTeam(team);
        player.sendMessage("&aJoined the queue!"); // Create a new team, add it to the game's arena and add the user to it
        plugin.getGameHandler().setGameState(game, GameState.QUEUE);
        if (canStart(game)) {
            plugin.getGameHandler().setGameState(game, GameState.ONGOING); // check if the game can start, if so start it
        }
    }

    /**
     * @param game game that will be started
     *             Method that deals witrh the starting of the game, giving the kits, etc.
     */
    public void startGame(final Game game) { // start the game, teleport every player to a different spawn
        Bukkit.getLogger().info("The game " + game + " has started!");
        final Queue<Location> locationTpQueue = new ArrayDeque<>(game.getArena().getSpawns());

        if (game.getArena().getSpawns().size() == 2) { // less spawns then players
            game.getQueue().forEach(queueMember -> {
                final Player player = queueMember.getBukkitPlayer();
                player.getInventory().clear();
                player.teleport(locationTpQueue.poll());
                player.setGameMode(GameMode.SURVIVAL);

                plugin.getKitManager().applyKitCorrectly(queueMember, game.getGameType()); // apply the kits
                if (game.isPrivate()) {
                    for (final String m : Messages.PRIVATE_GAME_STARTED) { // send a nice centered message for private games (no stats update addition)
                        queueMember.sendCenteredMessage(m.
                                replace("{opponents}", findGameOpponent(queueMember, game)).
                                replace("{gametype}", game.getGameType().getIdentifier()));
                    }
                } else {
                    for (final String m : Messages.GAME_STARTED) { // send a nice centered message
                        queueMember.sendCenteredMessage(m.
                                replace("{opponents}", findGameOpponent(queueMember, game)).
                                replace("{gametype}", game.getGameType().getIdentifier()));
                    }
                }

            });
            gameLoop(game);
        } else throw new InvalidSpawnsException("There are not enough spawns available!");

    }


    private boolean canStart(final Game game) {
        return game.getQueue().size() == 2;
    }

    /**
     * @param game   the game a player was removed from
     * @param player the player that got removed from the queue
     *               This method deals with the situation where a player leaves the queue
     */
    private void removeFromQueue(final Game game, final DuelPlayer player) {

        // remove a user from the queue, give them back the lobby items, and removing their team of the arena
        player.getInventory().clear();
        player.clearArmor();
        player.giveLobbyInv();
        player.fillHunger();
        player.clearEffects();
        plugin.getScoreboard().lobbyScoreboard(player.getBukkitPlayer());

        final Team team = game.getArena().getTeamByName(player.getName());
        game.getArena().getTeams().remove(team);
        game.getQueue().remove(player);
        player.setState(PlayerState.IN_LOBBY);
        player.setHasLost(false); // set them to the default false
        if (2 > game.getQueue().size()) { // end the game if there aren't enough players left
            Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getGameHandler().setGameState(game, GameState.END), 20L);
        }

    }

    private void gameLoop(final Game game) { // loop of the game, if the game takes longer then the max time, end it
        new BukkitRunnable() {
            @Override
            public void run() {
                if (game.getGameState() == GameState.ONGOING) {
                    if (game.getArena().getTimer() > 0) {
                        game.getArena().decrementTimer();
                    } else {
                        handleResult(game);
                        this.cancel();
                        plugin.getGameHandler().setGameState(game, GameState.END);
                        game.getArena().resetTimer();
                    }
                } else { // if the gamer ends before the timer ends, reset it, and cancel the game loop
                    this.cancel();
                    game.getArena().resetTimer();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    /**
     * @param duelPlayer the player we are trying to get the oponent from
     * @param game       the game the players are in
     * @return the name of the oponent
     */
    private String findGameOpponent(final DuelPlayer duelPlayer, final Game game) {
        // we're looking for the opponents from "duelPlayer"

        final StringBuilder oppName = new StringBuilder();
        for (final Team team : game.getArena().getTeams()) {
            if (!team.getMembers().contains(duelPlayer)) {
                team.getMembers().forEach(oppMember -> oppName.append(oppMember.getName()));
            }
        }

        return oppName.toString();
    }

    private boolean isWinner(final DuelPlayer duelPlayer) { // only called on the end, therefore we know atleast 1 player has NOT lost or there is a draw
        // draws are handles differently
        return !duelPlayer.hasLost();
    }

    private void teleportToLobby(final Game game) {
        game.getQueue().forEach(queueMember -> queueMember.getBukkitPlayer().teleport(
               Utils.stringToLoc(plugin.getConfig().getString("spawn").split(" "))));

    }

    public void applyScoreboard(final DuelPlayer duelPlayer, final Game game) { // apply the scoreboard
        new BukkitRunnable() {
            @Override
            public void run() { // if the game is ongoing, keep updating it every second
                if (duelPlayer.getState() == PlayerState.IN_GAME) {
                    plugin.getScoreboard().gameScoreboard(duelPlayer.getBukkitPlayer(), game);
                } else { // else, remove it
                    this.cancel();
                    plugin.getScoreboard().lobbyScoreboard(duelPlayer.getBukkitPlayer());
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private boolean isDraw(final Game game) { // both players are in the queue (no one died)
        return game.getQueue().stream().filter(duelPlayer -> !duelPlayer.hasLost()).count() == 2;
    }


}
