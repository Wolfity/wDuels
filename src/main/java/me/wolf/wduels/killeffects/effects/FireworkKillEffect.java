package me.wolf.wduels.killeffects.effects;

import me.wolf.wduels.killeffects.KillEffect;
import me.wolf.wduels.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class FireworkKillEffect extends KillEffect {
    public FireworkKillEffect() {
        super("firework");
    }

    @Override
    public void playKillEffect(Location location) {
        for (int i = 0; i < 3; i++) {
            location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        }
    }

    @Override
    public ItemStack getIconUnlocked() {
        return ItemUtils.createItem(Material.FIREWORK, "&cFirework Kill Effect");
    }
}
