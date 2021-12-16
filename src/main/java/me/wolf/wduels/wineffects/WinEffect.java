package me.wolf.wduels.wineffects;

import me.wolf.wduels.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class WinEffect implements Comparable<WinEffect> {

    private final String name;
    private String permission;
    private int duration;

    public WinEffect(final String name) {
        this.name = name;
        this.permission = "wduels.wineffect." + name;
        this.duration = 8;
    }

    public abstract void playWinEffect(final Location location);

    public abstract ItemStack getIconUnlocked();

    public ItemStack getIconLocked() {
        return ItemUtils.createItem(Material.BARRIER, "&cNot unlocked: &4" + name);
    }

    public String getName() {
        return name;
    }

    public void decrementDuration() {
        this.duration--;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public int getDuration() {
        return duration;
    }

    public void resetDuration() {
        this.duration = 8;
    }

    @Override
    public int compareTo(WinEffect o) {
        return name.compareTo(o.getName());
    }


}
