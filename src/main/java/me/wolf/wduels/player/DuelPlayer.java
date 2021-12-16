package me.wolf.wduels.player;

import me.wolf.wduels.killeffects.KillEffect;
import me.wolf.wduels.killeffects.effects.DefaultKillEffect;
import me.wolf.wduels.utils.ItemUtils;
import me.wolf.wduels.utils.Utils;
import me.wolf.wduels.wineffects.WinEffect;
import me.wolf.wduels.wineffects.effects.DefaultWinEffect;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;

import java.util.Objects;
import java.util.UUID;

public class DuelPlayer {

    private final UUID uuid;
    private int  wins, kills;
    private PlayerState state;
    private KillEffect killEffect;
    private WinEffect winEffect;
    private boolean hasLost;

    public DuelPlayer(final UUID uuid) {
        this.uuid = uuid;
        this.kills = this.getKills();
        this.wins = this.getWins();
        this.state = PlayerState.IN_LOBBY;
        this.killEffect = new DefaultKillEffect(); // set the default as basic
        this.winEffect = new DefaultWinEffect();
        this.hasLost = false;
    }

    public DuelPlayer(final UUID uuid, final int wins, final int kills, final KillEffect killEffect, final WinEffect winEffect) {
        this.uuid = uuid;
        this.wins = wins;
        this.kills = kills;
        this.killEffect = killEffect;
        this.winEffect = winEffect;
        this.state = PlayerState.IN_LOBBY;
        this.hasLost = false;
    }

    public KillEffect getKillEffect() {
        return killEffect;
    }

    public void setKillEffect(KillEffect killEffect) {
        this.killEffect = killEffect;
    }

    public UUID getUuid() {
        return uuid;
    }

    public PlayerState getState() {
        return state;
    }

    public void setState(PlayerState state) {
        this.state = state;
    }

    public String getName() {
        return Bukkit.getPlayer(getUuid()).getName();
    }

    public void sendMessage(final String msg) {
        Bukkit.getPlayer(getUuid()).sendMessage(Utils.colorize(msg));
    }

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(getUuid());
    }

    public Inventory getInventory() {
        return Bukkit.getPlayer(getUuid()).getInventory();
    }

    public int getWins() {
        return wins;
    }

    public int getKills() {
        return kills;
    }

    public void incrementKills() {
        this.kills++;
    }
    public void incrementWins() {
        this.wins++;
    }

    public void clearArmor() {
        Bukkit.getPlayer(getUuid()).getInventory().setHelmet(null);
        Bukkit.getPlayer(getUuid()).getInventory().setChestplate(null);
        Bukkit.getPlayer(getUuid()).getInventory().setLeggings(null);
        Bukkit.getPlayer(getUuid()).getInventory().setBoots(null);
    }

    public Location getLocation() {
        return Bukkit.getPlayer(getUuid()).getLocation();
    }

    public double getHealth() {
        return Bukkit.getPlayer(getUuid()).getHealth();
    }

    public void sendCenteredMessage(final String s) {
        Utils.sendCenteredMessage(Bukkit.getPlayer(uuid), Utils.colorize(s));
    }

    public void giveLobbyInv() {
        this.getInventory().addItem(ItemUtils.createItem(Material.DIAMOND_SWORD, "&aDuels &7(Click to queue)"));
        this.getInventory().addItem(ItemUtils.createItem(Material.BLAZE_POWDER, "&aKill Effects &7(Click to open)"));
        this.getInventory().addItem(ItemUtils.createItem(Material.FIREWORK, "&aWin Effects &7(Click to queue)"));
    }

    public void setHasLost(boolean hasLost) {
        this.hasLost = hasLost;
    }

    public boolean hasLost() {
        return hasLost;
    }

    public WinEffect getWinEffect() {
        return winEffect;
    }

    public void setWinEffect(WinEffect winEffect) {
        this.winEffect = winEffect;
    }

    public void fillHunger() {
        getBukkitPlayer().setFoodLevel(20);
        getBukkitPlayer().setSaturation(20);
    }

    public void clearEffects() {
        for(PotionEffect effect : getBukkitPlayer().getActivePotionEffects()) {
            getBukkitPlayer().removePotionEffect(effect.getType());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DuelPlayer that = (DuelPlayer) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
