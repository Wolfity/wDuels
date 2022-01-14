package me.wolf.wduels.listeners;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.managers.PlayerManager;
import me.wolf.wduels.player.DuelPlayer;
import me.wolf.wduels.player.PlayerState;
import me.wolf.wduels.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InventoryListeners implements Listener {

    private final DuelsPlugin plugin;

    public InventoryListeners(final DuelsPlugin plugin) {
        this.plugin = plugin;
    }

    // not supposed to drop anything in lobby
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!plugin.getPlayerManager().getDuelPlayers().containsKey(event.getPlayer().getUniqueId())) return;

        final DuelPlayer duelPlayer = plugin.getPlayerManager().getDuelPlayer(event.getPlayer().getUniqueId());

        event.setCancelled(duelPlayer.getState() == PlayerState.IN_LOBBY);

    }

    // you're not supposed to pick anything up
    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (!plugin.getPlayerManager().getDuelPlayers().containsKey(event.getPlayer().getUniqueId())) return;

        final DuelPlayer duelPlayer = plugin.getPlayerManager().getDuelPlayer(event.getPlayer().getUniqueId());

        event.setCancelled(duelPlayer.getState() == PlayerState.IN_LOBBY);

    }

    // players can move items in their inventory whilst ingame, not in lobby
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            if (plugin.getPlayerManager().getDuelPlayers().containsKey(event.getWhoClicked().getUniqueId())) {
                final DuelPlayer duelPlayer = plugin.getPlayerManager().getDuelPlayer(event.getWhoClicked().getUniqueId());
                if (duelPlayer.getState() != PlayerState.IN_LOBBY)
                    return; // in the "lobby" they aren't allowed to move their items.
                final List<ItemStack> items = new ArrayList<>();
                items.add(event.getCurrentItem());
                items.add(event.getCursor());
                items.add((event.getClick() == ClickType.NUMBER_KEY) ?
                        event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) : event.getCurrentItem());
                for (ItemStack item : items) {
                    if (item != null)
                        event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPrivGameMenuClose(InventoryCloseEvent event) {
        // if these menus are opened, we are certain they are loaded in DuelPlayers and are in the used Maps
        final UUID uuid = event.getPlayer().getUniqueId();
        final PlayerManager playerManager = plugin.getPlayerManager();
        final DuelPlayer sender = playerManager.getDuelPlayer(uuid); // one closing the inventory

        if (event.getInventory().getName() == null) return;

        if (event.getInventory().getName().equalsIgnoreCase(Utils.colorize("&aSelect a private duel type!"))) { // sender is canceling
            if (playerManager.getPrivateGameRequests().containsKey(sender)) { // sender as key, he is confirmed as the sender
                if (!plugin.getGameManager().isPlayerInQueue(sender)) { // the user is not in the queue yet i.o.w, he hasn't selected a game to duel the receiver in, remove from the appropriate map
                    sender.sendMessage("&cYou cancelled the request!");
                    playerManager.getPrivateGameRequests().remove(sender);
                }
            }

        }
    }

}


