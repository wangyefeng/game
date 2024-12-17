package org.game.logic.entity;

public abstract class Entity implements Cloneable {

    public Entity clone() {
        try {
            return (Entity) super.clone();
        } catch (CloneNotSupportedException e) {
            // This should never happen
            return null;
        }
    }

}
