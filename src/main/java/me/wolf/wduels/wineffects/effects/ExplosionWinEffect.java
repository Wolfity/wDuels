package me.wolf.wduels.wineffects.effects;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.utils.ItemUtils;
import me.wolf.wduels.wineffects.WinEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

public class ExplosionWinEffect extends WinEffect {
    private final DuelsPlugin plugin;
    public ExplosionWinEffect(final DuelsPlugin plugin) {
        super("explosion");
        this.plugin = plugin;
    }

    @Override
    public void playWinEffect(Location location) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(getDuration() > 0) {
                    decrementDuration();
                    new ParticleBuilder(ParticleEffect.EXPLOSION_HUGE, location)
                            .setOffsetX(3)
                            .setOffsetY(3)
                            .setOffsetZ(3)
                            .setAmount(5)
                            .display();
                } else {
                    this.cancel();
                    resetDuration();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);

    }

    @Override
    public ItemStack getIconUnlocked() {
        return ItemUtils.createItem(Material.TNT, "&aExplosion Win Effect");
    }
}
