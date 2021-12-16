package me.wolf.wduels.killeffects.effects;

import me.wolf.wduels.killeffects.KillEffect;
import me.wolf.wduels.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DefaultKillEffect extends KillEffect {

    public DefaultKillEffect() {
        super("default");
    }

    @Override
    public void setPermission(String permission) {
        super.setPermission("");
    }

    @Override
    public void playKillEffect(Location location) {

    }


    @Override
    public ItemStack getIconUnlocked() {
        return ItemUtils.createItem(Material.DIRT, "&7Default");
    }
}
