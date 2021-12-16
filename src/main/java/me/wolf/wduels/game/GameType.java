package me.wolf.wduels.game;

import me.wolf.wduels.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum GameType {

    NO_DEBUFF("No Debuff", ItemUtils.createItem(Material.DIAMOND_SWORD, "&bNo Debuff")),
    DEBUFF("Debuff", ItemUtils.createItem(Material.DIAMOND_HELMET, "&bDebuff")),
    DIAMOND("Diamond", ItemUtils.createItem(Material.DIAMOND_CHESTPLATE, "&bDiamond")),
    ARCHER("Archer", ItemUtils.createItem(Material.BOW, "&bArcher")),
    UHC("UHC", ItemUtils.createItem(Material.GOLDEN_APPLE, "&bUHC")),
    COMBO("Combo", ItemUtils.createItem(Material.DIAMOND_CHESTPLATE, "&bCombo")),
    CLASSIC("Classic", ItemUtils.createItem(Material.FLINT_AND_STEEL, "&bClassic"));


    private final String identifier;
    private final ItemStack icon;

    GameType(final String identifier, final ItemStack icon) {
        this.identifier = identifier;
        this.icon = icon;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public String getIdentifier() {
        return identifier;
    }
}
