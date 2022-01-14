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

public class AngryVillagerWinEffect extends WinEffect {
    private final DuelsPlugin plugin;

    public AngryVillagerWinEffect(final DuelsPlugin plugin) {
        super("angryvillager");
        this.plugin = plugin;
    }

    @Override
    public void playWinEffect(Location location) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getDuration() > 0) {
                    decrementDuration();
                    new ParticleBuilder(ParticleEffect.VILLAGER_ANGRY, location)
                            .setAmount(30)
                            .setOffsetX(5)
                            .setOffsetY(3)
                            .setOffsetZ(5)
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
        return ItemUtils.createItem(Material.CARROT_ITEM, "&cAngry Villager Win Effect");
    }
}
