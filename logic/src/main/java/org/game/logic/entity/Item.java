package org.game.logic.entity;

public class Item implements Cloneable {
    private int id;
    private int num;

    public Item(int id, int num) {
        if (num < 0) {
            throw new IllegalArgumentException("num must be non-negative");
        }
        this.id = id;
        this.num = num;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void addNum(int num) {
        this.num += num;
    }

    @Override
    protected Item clone() {
        try {
            return (Item) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
