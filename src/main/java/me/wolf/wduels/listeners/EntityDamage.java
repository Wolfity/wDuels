package me.wolf.wduels.listeners;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.game.Game;
import me.wolf.wduels.game.GameState;
import me.wolf.wduels.game.GameType;
import me.wolf.wduels.player.DuelPlayer;
import me.wolf.wduels.player.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamage implements Listener {

    private final DuelsPlugin plugin;

    public EntityDamage(final DuelsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        final DuelPlayer damager = plugin.getPlayerManager().getDuelPlayer(event.getDamager().getUniqueId());
        final DuelPlayer damaged = plugin.getPlayerManager().getDuelPlayer(event.getEntity().getUniqueId());

        if (damaged == null || damager == null) { // if one of the two is null, cancel it.
            event.setCancelled(true);
        } else if (damaged.getState() != PlayerState.IN_GAME || damager.getState() != PlayerState.IN_GAME) {
            event.setCancelled(true);
        }

        final Game game = plugin.getGameManager().getGameByPlayer(damaged);
        if (game == null) return;
        if (game.getGameType() == GameType.COMBO) { // set the max no damage ticks to 1 (combo)
            // allows you to hit the player more per tick
            if (damager == null) return;
            if (event.getDamager().getUniqueId().equals(damager.getUuid())) {
                ((Player) event.getEntity()).setMaximumNoDamageTicks(1);
            }
        } else { // setting it back to default
            ((Player) event.getEntity()).setMaximumNoDamageTicks(20);
        }

        event.setCancelled(game.getGameState() == GameState.END || game.getGameState() == GameState.QUEUE); // if the game ends in a draw, cancel damage after the ending
    }

}
