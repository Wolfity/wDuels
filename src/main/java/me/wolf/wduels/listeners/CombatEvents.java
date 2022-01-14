package me.wolf.wduels.listeners;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.game.Game;
import me.wolf.wduels.player.DuelPlayer;
import me.wolf.wduels.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Optional;

public class CombatEvents implements Listener {

    private final DuelsPlugin plugin;

    public CombatEvents(final DuelsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (plugin.getPlayerManager().getDuelPlayer(event.getEntity().getUniqueId()) == null) return;
        final DuelPlayer duelPlayer = plugin.getPlayerManager().getDuelPlayer(event.getEntity().getUniqueId());

        boolean isLavaDeath = Optional.ofNullable(event.getEntity().getLastDamageCause())
                .map(EntityDamageEvent::getCause)
                .filter(cause -> cause.equals(EntityDamageEvent.DamageCause.LAVA))
                .isPresent(); // checking if the player died to lava

        boolean isDrownDeath = Optional.ofNullable(event.getEntity().getLastDamageCause())
                .map(EntityDamageEvent::getCause)
                .filter(cause -> cause.equals(EntityDamageEvent.DamageCause.DROWNING))
                .isPresent(); // checking if the player died due to drowning


        final Game game = plugin.getGameManager().getGameByPlayer(duelPlayer);

        if (isLavaDeath || isDrownDeath) {
            event.getDrops().clear(); // clear the drops, we don't want drops
            duelPlayer.fillHunger();
            duelPlayer.clearEffects();
            plugin.getGameManager().handleGameKill(duelPlayer, game);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) { // player dies = game over
        if (plugin.getPlayerManager().getDuelPlayer(event.getEntity().getUniqueId()) == null) return;
        final DuelPlayer killed = plugin.getPlayerManager().getDuelPlayer(event.getEntity().getUniqueId());
        if (event.getEntity().getKiller() == null) return; // lava in UHC for example.
        final DuelPlayer killer = plugin.getPlayerManager().getDuelPlayer(event.getEntity().getKiller().getUniqueId());

        event.getDrops().clear(); // clear the drops, we don't want drops
        killer.getKillEffect().playKillEffect(killed.getLocation()); // play the killer's selected kill/win effect
        killer.getWinEffect().playWinEffect(killer.getLocation());

        if (!plugin.getGameManager().getGameByPlayer(killer).isPrivate()) {
            killer.incrementKills(); // add to their stats
            killer.incrementWins();
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> { // handle the game kill, and respawn the user
            event.getEntity().spigot().respawn();
            plugin.getGameManager().handleGameKill(killed, plugin.getGameManager().getGameByPlayer(killer));
        }, 10);

    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) { // setting the respawn location to the duels lobby
        if (plugin.getPlayerManager().getDuelPlayer(event.getPlayer().getUniqueId()) == null) return;
        plugin.getScoreboard().lobbyScoreboard(event.getPlayer());
        event.setRespawnLocation(Utils.stringToLoc(plugin.getConfig().getString("spawn").split(" ")));

    }


}


