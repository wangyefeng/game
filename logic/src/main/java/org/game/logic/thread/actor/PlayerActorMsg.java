package org.game.logic.thread.actor;

public class PlayerActorMsg {

    private final int playerId;
    private final Runnable action;
    private final boolean isOffline;

    public PlayerActorMsg(int playerId, Runnable action, boolean isOffline) {
        this.playerId = playerId;
        this.action = action;
        this.isOffline = isOffline;
    }

    public PlayerActorMsg(int playerId, Runnable action) {
        this(playerId, action, false);
    }

    public int getPlayerId() {
        return playerId;
    }

    public Runnable getAction() {
        return action;
    }

    public boolean isOffline() {
        return isOffline;
    }
}
