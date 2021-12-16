package me.wolf.wduels.killeffects.effects;


import me.wolf.wduels.killeffects.KillEffect;
import me.wolf.wduels.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;


public class ExplosionKillEffect extends KillEffect {
    public ExplosionKillEffect() {
        super("explosion");
    }


    @Override
    public void playKillEffect(Location location) {
        new ParticleBuilder(ParticleEffect.EXPLOSION_HUGE, location)
                .setAmount(1)
                .setLocation(location).display();
    }

    @Override
    public ItemStack getIconUnlocked() {
        return ItemUtils.createItem(Material.TNT, "&cExplosion Kill Effect");
    }
}
