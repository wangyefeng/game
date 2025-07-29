package org.game.cache;

public abstract class GameEntity implements Cloneable {

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // ignore
            return null;
        }
    }
}