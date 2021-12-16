package me.wolf.wduels.game.games;

import me.wolf.wduels.arena.Arena;
import me.wolf.wduels.game.Game;
import me.wolf.wduels.game.GameType;

public class PrivateGame extends Game {

    private boolean isAccepted; //represents whether a private game invite has been accepted or not
    private int inviteExpire;


    public PrivateGame(Arena arena, GameType gameType) {
        super(arena, gameType);
        this.isAccepted = false;
        this.inviteExpire = 30;
    }

    public int getInviteExpire() {
        return this.inviteExpire;
    }

    public void resetTimer(int time) {
        this.inviteExpire = time;
    }

    public void decrementInviteExpire() {
        this.inviteExpire--;
    }

    public void cancelTimer() {
        this.inviteExpire = 0;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    @Override
    public boolean isPrivate() {
        return true;
    }
}
