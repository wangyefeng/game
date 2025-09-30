package org.wyf.game.logic.player.item;

import org.wyf.game.config.entity.Item;

public class AddableItem implements Item {

    private final int id;

    private long num;

    public AddableItem(int id, long num) {
        this.id = id;
        this.num = num;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public long num() {
        return num;
    }

    public void add(long num) {
        this.num += num;
    }
}
