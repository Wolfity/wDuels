package me.wolf.wduels.managers;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.wineffects.WinEffect;
import me.wolf.wduels.wineffects.effects.AngryVillagerWinEffect;
import me.wolf.wduels.wineffects.effects.DefaultWinEffect;
import me.wolf.wduels.wineffects.effects.ExplosionWinEffect;
import me.wolf.wduels.wineffects.effects.FireworkWinEffect;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WinEffectManager {

    private final DuelsPlugin plugin;
    private final Set<WinEffect> winEffects = new HashSet<>();

    public WinEffectManager(final DuelsPlugin plugin) {
        this.plugin = plugin;
    }

    public WinEffect getWinEffectByName(final String identifier) {
        return winEffects.stream().filter(winEffect -> winEffect.getName().equalsIgnoreCase(identifier)).findFirst().orElse(null);
    }

    public void loadWinEffects() { // cache win effects
        addWinEffects(new DefaultWinEffect(), new ExplosionWinEffect(plugin), new FireworkWinEffect(plugin), new AngryVillagerWinEffect(plugin));
    }

    private void addWinEffects(final WinEffect... winEffects) {
        this.winEffects.addAll(Arrays.asList(winEffects));
    }

    public Set<WinEffect> getWinEffects() {
        return winEffects;
    }

    public WinEffect getDefaultEffect() {
        return getWinEffectByName("default");
    }

}
