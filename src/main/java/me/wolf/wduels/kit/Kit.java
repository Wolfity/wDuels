package me.wolf.wduels.kit;

import me.wolf.wduels.game.GameType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Kit {


    private List<ItemStack> kitItems;
    private final GameType gameType;

    public Kit(final GameType gameType) {
        this.gameType = gameType;
        this.kitItems = new ArrayList<>();
    }

    public List<ItemStack> getKitItems() {
        return kitItems;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setKitItems(List<ItemStack> kitItems) {
        this.kitItems = kitItems;
    }

    public String getName() {
        return gameType.getIdentifier();
    }

}
