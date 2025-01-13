package org.game.logic.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public abstract class Entity implements Cloneable {

    @Id
    private int playerId;

    public Entity(int playerId) {
        this.playerId = playerId;
    }

    public Entity clone() throws CloneNotSupportedException {
        return (Entity) super.clone();
    }

    public int getPlayerId() {
        return playerId;
    }
}
