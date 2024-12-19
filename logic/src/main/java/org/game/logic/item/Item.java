package org.game.logic.item;

public record Item(int id, int num) {
    public Item {
        if (num < 0) {
            throw new IllegalArgumentException("num must be non-negative");
        }
    }
}
