package me.wolf.wduels.killeffects;

import me.wolf.wduels.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class KillEffect implements Comparable<KillEffect> {

    private final String name;
    private String  permission;

    public KillEffect(final String name) {
        this.name = name;
        this.permission = "wduels.killeffect." + name;

    }

    public abstract void playKillEffect(final Location location);

    public abstract ItemStack getIconUnlocked();

    public ItemStack getIconLocked() {
        return ItemUtils.createItem(Material.BARRIER, "&cNot unlocked: &4" + name);
    } // default, if you do not have the permission for this killeffect, this is the itemstack that will appear

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }


    @Override
    public int compareTo(KillEffect o) {
        return name.compareTo(o.getName());
    }
}
