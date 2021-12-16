package me.wolf.wduels.arena;

public enum ArenaState {

    PREQUEUE, // No need to interact w. the game handler, this is a default state
    QUEUE, // Default arena state
    ONGOING, // the game is on going
    END // end of the game, win effects playing

}
