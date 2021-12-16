package me.wolf.wduels.listeners;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.player.DuelPlayer;
import me.wolf.wduels.player.PlayerState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevel implements Listener {

    private final DuelsPlugin plugin;
    public FoodLevel(final DuelsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFoodStatusChange(FoodLevelChangeEvent event) {
        if (plugin.getPlayerManager().getDuelPlayer(event.getEntity().getUniqueId()) == null) return;
            final DuelPlayer player = plugin.getPlayerManager().getDuelPlayer(event.getEntity().getUniqueId());
            if(player.getState() != PlayerState.IN_GAME) { // set the food to max unless ingame
                player.fillHunger();
            }

    }

}
