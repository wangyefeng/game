package org.game.logic.database.entity;

public abstract class Entity implements Cloneable {

    protected Entity() {
    }

    public Entity clone() throws CloneNotSupportedException {
        return (Entity) super.clone();
    }

    public abstract int getPlayerId();
}
