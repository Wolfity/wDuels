package me.wolf.wduels.killeffects.effects;

import me.wolf.wduels.killeffects.KillEffect;
import me.wolf.wduels.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

public class HeartsKillEffect extends KillEffect {

    public HeartsKillEffect() {
        super("hearts");
    }

    @Override
    public void playKillEffect(Location location) {
        new ParticleBuilder(ParticleEffect.HEART, location)
                .setAmount(30)
                .setOffsetX(0.5f)
                .setOffsetY(1f)
                .setOffsetZ(0.5f)
                .display();
    }

    @Override
    public ItemStack getIconUnlocked() {
        return ItemUtils.createItem(Material.GOLDEN_APPLE, "&cHearts Kill Effect");
    }
}
