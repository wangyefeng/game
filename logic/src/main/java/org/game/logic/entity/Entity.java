package org.game.logic.entity;

import org.springframework.data.annotation.Id;

public abstract class Entity implements Cloneable {

    @Id
    private int id;

    public Entity(int id) {
        this.id = id;
    }

    public Entity clone() {
        try {
            return (Entity) super.clone();
        } catch (CloneNotSupportedException e) {
            // This should never happen
            return null;
        }
    }

    public int getId() {
        return id;
    }
}
