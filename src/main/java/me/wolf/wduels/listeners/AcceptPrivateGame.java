package me.wolf.wduels.listeners;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.game.Game;
import me.wolf.wduels.game.games.PrivateGame;
import me.wolf.wduels.player.DuelPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class AcceptPrivateGame implements Listener {

    private DuelsPlugin plugin;

    public AcceptPrivateGame(final DuelsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAcceptCommand(PlayerCommandPreprocessEvent event) { // handles the accepting of the private duel request
        final DuelPlayer receiver = plugin.getPlayerManager().getDuelPlayer(event.getPlayer().getUniqueId());
        final DuelPlayer sender = plugin.getPlayerManager().getPrivateGameRequestReceiver(plugin.getPlayerManager().getPrivateGameRequests(), receiver);
        if (!event.getMessage().contains("/duels accept")) return;

        if (sender != null) { // the sender exists, in other words, a duel request is active!
            final Game game = plugin.getGameManager().getGameByPlayer(sender);
            if (event.getMessage().equalsIgnoreCase("/duels accept " + sender.getName() + " " + game.getGameType().getIdentifier())) {
                ((PrivateGame) game).setAccepted(true);
                ((PrivateGame) game).cancelTimer();

                plugin.getGameManager().joinQueue(game, receiver);
                plugin.getPlayerManager().getPrivateGameRequests().remove(sender); // remove both sender and receiver from the original priv game requests map
            } else receiver.sendMessage("&4Something went wrong");
        } else receiver.sendMessage("&cInvite expired!");

    }

}
