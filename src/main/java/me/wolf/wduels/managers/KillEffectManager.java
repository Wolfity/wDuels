package me.wolf.wduels.managers;

import me.wolf.wduels.killeffects.KillEffect;
import me.wolf.wduels.killeffects.effects.DefaultKillEffect;
import me.wolf.wduels.killeffects.effects.ExplosionKillEffect;
import me.wolf.wduels.killeffects.effects.FireworkKillEffect;
import me.wolf.wduels.killeffects.effects.HeartsKillEffect;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class KillEffectManager {


    private final Set<KillEffect> killEffects = new HashSet<>();

    public KillEffect getKillEffectByName(final String identifier) {
        return killEffects.stream().filter(killEffect -> killEffect.getName().equalsIgnoreCase(identifier)).findFirst().orElse(null);
    }

    public void loadKillEffects() { // cache all killeffects
        addKillEffects(new DefaultKillEffect(), new ExplosionKillEffect(), new FireworkKillEffect(), new HeartsKillEffect());
    }

    private void addKillEffects(final KillEffect... killEffects) {
        this.killEffects.addAll(Arrays.asList(killEffects));
    }

    public Set<KillEffect> getKillEffects() {
        return killEffects;
    }

    public KillEffect getDefaultEffect() {
        return getKillEffectByName("default");
    }


}
