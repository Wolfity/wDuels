package me.wolf.wduels.constants;

import me.wolf.wduels.utils.Utils;

public final class Messages {


    public static final String ARENA_CREATED = Utils.colorize(
            "&aSuccessfully created the arena {arena}");

    public static final String ARENA_DELETED = Utils.colorize(
            "&cSuccessfully deleted the arena {arena}");

    public static final String SET_LOBBY_SPAWN = Utils.colorize(
            "&aSuccessfully set the lobby spawn");

    public static final String ARENA_NOT_FOUND = Utils.colorize(
            "&cThis arena does not exist!");

    public static final String LEFT_DUELS = Utils.colorize(
            "&cSuccessfully left duels!");

    public static final String JOINED_DUELS = Utils.colorize(
            "&aSuccessfully joined duels!");

    public static final String ARENA_EXISTS = Utils.colorize("&cThis arena already exists!");

    public static final String ADDED_SPAWN = Utils.colorize("&aAdded a spawn for &2{arena}");

    public static final String ALREADY_IN_DUELS = Utils.colorize("&aYou are already in the duels gamemode!");

    public static final String NOT_IN_DUELS = Utils.colorize("&aYou are currently not the duels gamemode!");

    public static final String[] GAME_STARTED = new String[]{
            "&a-----------------------------------------------------",
            "&2&l The game has started, good luck!",
            "&7Opponents: &c{opponents}",
            "&7Gamemode: &c{gametype}",
            " ",
            "&7May the best win!",
            "&a-----------------------------------------------------"
    };

    public static final String[] PRIVATE_GAME_STARTED = new String[]{
            "&a-----------------------------------------------------",
            "&c&lThis is a private Game, stats will not be updated!",
            "&2&l The game has started, good luck!",
            "&7Opponents: &c{opponents}",
            "&7Gamemode: &c{gametype}",
            " ",
            "&7May the best win!",
            "&a-----------------------------------------------------"
    };

    public static final String[] GAME_ENDED = new String[]{
            "&8-----------------------------------------------------",
            "&c&lGame Over",
            "&7You &6&l{result}",
            "&7Good Fight",
            "&8-----------------------------------------------------"
    };

    private Messages() {
    }
}
