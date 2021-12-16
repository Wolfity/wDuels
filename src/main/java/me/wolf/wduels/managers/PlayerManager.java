package me.wolf.wduels.managers;

import me.wolf.wduels.killeffects.KillEffect;
import me.wolf.wduels.player.DuelPlayer;
import me.wolf.wduels.wineffects.WinEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private final Map<UUID, DuelPlayer> duelPlayers = new HashMap<>();
    private final Map<DuelPlayer, DuelPlayer> privateGameRequests = new HashMap<>(); // Map <Sender, Receiver>

    public void addDuelPlayer(final UUID uuid) {
        final DuelPlayer duelPlayer = new DuelPlayer(uuid);
        this.duelPlayers.put(uuid, duelPlayer);
    }

    public void addDuelPlayer(final UUID uuid, final int wins, final int kills, final KillEffect killEffect, final WinEffect winEffect) {
        this.duelPlayers.put(uuid, new DuelPlayer(uuid, wins, kills, killEffect, winEffect));
    }

    public void removeDuelPlayer(final UUID uuid) {
        this.duelPlayers.remove(uuid);
    }

    public DuelPlayer getDuelPlayer(final UUID uuid) {
        return duelPlayers.get(uuid);
    }

    public Map<UUID, DuelPlayer> getDuelPlayers() {
        return duelPlayers;
    }

    public Map<DuelPlayer, DuelPlayer> getPrivateGameRequests() {
        return privateGameRequests;
    }

    public <K, V> K getPrivateGameRequestReceiver(Map<K, V> map, V value) { // get the sender by passing in the receiver
        return map.entrySet()
                .stream()
                .filter(entry -> value.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);
    }


}
