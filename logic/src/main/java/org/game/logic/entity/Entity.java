package org.game.logic.entity;

import org.springframework.data.annotation.Id;

public abstract class Entity implements Cloneable {

    @Id
    private int id;

    protected Entity() {
    }

    public Entity(int id) {
        this.id = id;
    }

    public Entity clone() throws CloneNotSupportedException {
        return (Entity) super.clone();
    }

    public int getId() {
        return id;
    }
}
