package me.wolf.wduels.listeners;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.player.DuelPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreak implements Listener {

    private final DuelsPlugin plugin;

    public BlockBreak(final DuelsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        if(!plugin.getPlayerManager().getDuelPlayers().containsKey(player.getUniqueId())) return;

        final DuelPlayer duelPlayer = plugin.getPlayerManager().getDuelPlayer(player.getUniqueId());
        event.setCancelled(duelPlayer != null);

    }

}
