package me.wolf.wduels.listeners;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.player.DuelPlayer;
import me.wolf.wduels.player.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    private final DuelsPlugin plugin;
    public PlayerQuit(final DuelsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if(!plugin.getPlayerManager().getDuelPlayers().containsKey(event.getPlayer().getUniqueId())) return;
        final DuelPlayer player = plugin.getPlayerManager().getDuelPlayer(event.getPlayer().getUniqueId());


        // check if the player is ingame, handle their disconnection as a leave
        if(player.getState() != PlayerState.IN_LOBBY) {
           plugin.getGameManager().handleGameLeave(plugin.getGameManager().getGameByPlayer(player), player);
        // saving data to the database
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getSqLiteManager().saveData(player));
        }


    }

}
