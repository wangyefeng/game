package org.wyf.game.logic.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseInfo implements Cloneable {

    @Id
    @Column(nullable = false)
    private int playerId;

    protected BaseInfo() {
    }

    public BaseInfo(int playerId) {
        this.playerId = playerId;
    }

    public BaseInfo clone() throws CloneNotSupportedException {
        return (BaseInfo) super.clone();
    }

    public int getPlayerId() {
        return playerId;
    }
}
