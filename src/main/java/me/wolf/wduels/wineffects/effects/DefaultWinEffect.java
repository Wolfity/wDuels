package me.wolf.wduels.wineffects.effects;

import me.wolf.wduels.utils.ItemUtils;
import me.wolf.wduels.wineffects.WinEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DefaultWinEffect extends WinEffect {
    public DefaultWinEffect() {
        super("default");

    }

    @Override
    public void playWinEffect(Location location) {

    }

    @Override
    public ItemStack getIconUnlocked() {
        return ItemUtils.createItem(Material.DIRT, "&aDefault Win Effect");
    }

    @Override
    public void setPermission(String permission) {
        super.setPermission("");
    }
}
