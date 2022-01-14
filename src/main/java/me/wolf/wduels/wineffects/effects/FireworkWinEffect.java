package me.wolf.wduels.wineffects.effects;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.utils.ItemUtils;
import me.wolf.wduels.wineffects.WinEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

import java.awt.*;

public class FireworkWinEffect extends WinEffect {
    private final DuelsPlugin plugin;

    public FireworkWinEffect(final DuelsPlugin plugin) {
        super("firework");
        this.plugin = plugin;
    }

    @Override
    public void playWinEffect(Location location) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getDuration() > 0) {
                    decrementDuration();
                    location.getWorld().spawnEntity(location, EntityType.FIREWORK);
                    new ParticleBuilder(ParticleEffect.FIREWORKS_SPARK, location)
                            .setAmount(5)
                            .setOffsetX(3)
                            .setOffsetY(3)
                            .setOffsetZ(3)
                            .setColor(Color.GREEN)
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
        return ItemUtils.createItem(Material.FIREWORK, "&aFirework Win Effect");
    }
}
