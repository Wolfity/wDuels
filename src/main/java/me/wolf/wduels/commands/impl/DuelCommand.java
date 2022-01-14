package me.wolf.wduels.commands.impl;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.commands.SubCommand;
import me.wolf.wduels.game.GameType;
import me.wolf.wduels.player.DuelPlayer;
import me.wolf.wduels.player.PlayerState;
import me.wolf.wduels.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class DuelCommand extends SubCommand {
    @Override
    protected String getCommandName() {
        return "duel";
    }

    @Override
    protected String getUsage() {
        return "&a/duels duel <player>";
    }

    @Override
    protected String getDescription() {
        return "&7Send a private duel request to a specific player";
    }

    @Override
    protected void executeCommand(Player player, String[] args, DuelsPlugin plugin) {
        if (args.length != 2) {
            player.sendMessage(Utils.colorize(getUsage()));
            return;
        }
        final Player receiver = Bukkit.getPlayerExact(args[1]);
        if(receiver != null) {
            if (canInvite(player, receiver, plugin)) {
                final DuelPlayer sender = plugin.getPlayerManager().getDuelPlayer(player.getUniqueId());
                final DuelPlayer duelsReceiver = plugin.getPlayerManager().getDuelPlayer(receiver.getUniqueId());
                sendRequest(sender, duelsReceiver, plugin);
            }
        } else player.sendMessage(Utils.colorize("&cThis user does not exist!"));

    }

    private void sendRequest(final DuelPlayer sender, final DuelPlayer receiver, final DuelsPlugin plugin) {
        plugin.getPlayerManager().getPrivateGameRequests().put(sender, receiver);
        openMenu(sender.getBukkitPlayer());

    }

    private void openMenu(final Player player) {
        final Inventory inventory = Bukkit.createInventory(null, 27, Utils.colorize("&aSelect a private duel type!"));

        for (final GameType type : GameType.values()) {
            inventory.addItem(type.getIcon());
        }
        player.openInventory(inventory);
    }

    /**
     * @param playerSender   the sender of the private duel request
     * @param playerReceived the receiver of the request
     * @return true if the invite can be sent, false if not
     * This method performs checks on both the sender, null checks, state checks, etc. If all are passed, the invite can be sent.
     */
    private boolean canInvite(final Player playerSender, final Player playerReceived, final DuelsPlugin plugin) {
        final DuelPlayer received = plugin.getPlayerManager().getDuelPlayer(playerReceived.getUniqueId());
        final DuelPlayer sender = plugin.getPlayerManager().getDuelPlayer(playerSender.getUniqueId());
        if (sender == null) { // check if hte sender is in duels
            playerSender.sendMessage(Utils.colorize("&cYou must be in duels (/join duels) to execute this command"));
            return false;
        } else if (received == null) { // check if the receiver exists/is in duels
            sender.sendMessage("&cThis player is invalid");
            return false;
        } else if (sender.getUuid().equals(received.getUuid())) { // check if you are not inviting yourself
            sender.sendMessage("&cYou can not invite yourself");
            return false;
        } else if (plugin.getPlayerManager().getPrivateGameRequests().containsKey(sender)) { // check if there are no current pending requests on your side
            sender.sendMessage("&cYou already have a pending duel request, wait for it to expire!");
            return false;
        } else if (sender.getState() != PlayerState.IN_LOBBY || received.getState() != PlayerState.IN_LOBBY) { // check if both are in lobby
            sender.sendMessage("&cYou or this user is not in the right state to send this user a duel!");
            return false;
        } else {
            return true;
        }
    }
}
