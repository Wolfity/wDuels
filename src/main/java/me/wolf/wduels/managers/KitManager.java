package me.wolf.wduels.managers;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.file.YamlConfig;
import me.wolf.wduels.game.GameType;
import me.wolf.wduels.kit.Kit;
import me.wolf.wduels.player.DuelPlayer;
import me.wolf.wduels.utils.ItemUtils;
import me.wolf.wduels.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KitManager {

    private final DuelsPlugin plugin;

    public KitManager(final DuelsPlugin plugin) {
        this.plugin = plugin;
    }

    private final Set<Kit> kits = new HashSet<>();


    public void loadKits(final YamlConfig cfg) { // load in all kits (kits.yml)
        try {
            for (final String kit : cfg.getConfig().getConfigurationSection("kits").getKeys(false)) { // loop section (all kits)
                final Kit loadKit = new Kit(GameType.valueOf(cfg.getConfig().getString("kits." + kit + ".game-type").toUpperCase()));
                final boolean hasPotions = cfg.getConfig().getBoolean("kits." + kit + ".has-potions");
                final List<ItemStack> materials = new ArrayList<>();

                for (final String item : cfg.getConfig().getConfigurationSection("kits." + kit + ".items").getKeys(false)) {
                    // loop kit (everythigg inside a kit), for example, material, name, etc..
                    final ConfigurationSection section = cfg.getConfig().getConfigurationSection("kits." + kit + ".items." + item);

                    final int amount = section.getInt("amount");
                    final String name = section.getString("name");
                    final boolean hasEnchantment = section.getBoolean("has-enchants");

                    if (section.getString("material").equalsIgnoreCase("ENCHANTED_GOLDEN_APPLE")) { // give ench golden apples
                        materials.add(ItemUtils.createItem(Material.GOLDEN_APPLE, name, amount, (short) 1));

                    } else {

                        final Material material = Material.valueOf(section.getString("material"));

                        if (!hasEnchantment) { // nesting the potions because they cant have enchants
                            if (!hasPotions) {
                                materials.add(ItemUtils.createItem(material, name, amount));
                            } else { // we'll need to do some different type of loading data due to potions
                                final boolean isPotion = section.getBoolean("is-potion");
                                final boolean fillLeftOverSpaces = section.getBoolean("fill-empty-spaces");
                                final short potionData = (short) section.getDouble("data");
                                if (!isPotion) {
                                    materials.add(ItemUtils.createItem(material, name, amount));
                                } else {
                                    if (!fillLeftOverSpaces) {
                                        materials.add(ItemUtils.createItem(material, name, amount, potionData));
                                    } else {
                                        fillSlots(ItemUtils.createItem(material, name, amount, potionData), materials);
                                    }
                                }

                            }
                        } else { // if the item has enchantments
                            final String enchString = section.getString("enchants");
                            final String[] splitEnchants = enchString.split(";"); // splits into ENCH:level
                            final ItemStack is = ItemUtils.createItem(material, name, amount);
                            for (final String ench : splitEnchants) {
                                final String[] finalEnch = ench.split(":");
                                is.addUnsafeEnchantment(Enchantment.getByName(finalEnch[0]), Integer.parseInt(finalEnch[1]));

                            }
                            materials.add(is);
                        }
                    }
                }

                loadKit.setKitItems(materials);
                kits.add(loadKit);
            }

        } catch (NullPointerException e) {
            Bukkit.getServer().getLogger().info(Utils.colorize("&4No Kits loaded!"));
            e.printStackTrace();
        }
    }


    public Set<Kit> getKits() {
        return kits;
    }

    public Kit getKitByGameType(final GameType gameType) { // grab a kit by it's gametype
        return kits.stream().filter(kit -> kit.getGameType() == gameType).findFirst().orElse(null);

    }

    public void applyKitCorrectly(final DuelPlayer duelPlayer, final GameType gameType) {
        // give users their items, apply armor and fill the rest of the inventory, in case that is selected
        final Kit kit = getKitByGameType(gameType);
        kit.getKitItems().forEach(item -> {
            final Player player = duelPlayer.getBukkitPlayer();

            if (ItemUtils.isArmor(item.getType())) {
                if (ItemUtils.isHelmet(item.getType())) {
                    player.getInventory().setHelmet(item);
                } else if (ItemUtils.isChestplate(item.getType())) {
                    player.getInventory().setChestplate(item);
                } else if (ItemUtils.isLeggings(item.getType())) {
                    player.getInventory().setLeggings(item);
                } else if (ItemUtils.isBoots(item.getType())) {
                    player.getInventory().setBoots(item);
                }
             } else player.getInventory().addItem(item);

        });

    }


    private void fillSlots(final ItemStack is, final List<ItemStack> kitItems) {
        int slotsToFill = 40 - kitItems.size(); // origin size of the inventory + 4 (armor)

        for (int i = 0; i < slotsToFill; i++) {
            kitItems.add(is);
        }
    }


}
