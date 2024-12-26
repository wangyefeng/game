package org.game.logic.item;

import org.game.config.entity.Item;

public class AddableItem implements Item {

    private final int id;

    private int num;

    public AddableItem(int id, int num) {
        this.id = id;
        this.num = num;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public int num() {
        return num;
    }

    public void add(int num) {
        this.num += num;
    }
}
