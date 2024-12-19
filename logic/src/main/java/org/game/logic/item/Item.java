package org.game.logic.item;

public class Item {

    private final int id;

    private int num;

    public Item(int id, int num) {
        this.id = id;
        this.num = num;
    }

    public int id() {
        return id;
    }

    public int num() {
        return num;
    }

    public void add(int num) {
        this.num += num;
    }
}
