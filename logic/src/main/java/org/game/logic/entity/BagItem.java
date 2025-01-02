package org.game.logic.entity;

import org.game.config.entity.Item;

/**
 * 背包中的物品
 */
public class BagItem implements Cloneable {
    private int id;
    private int num;

    BagItem() {
    }

    public BagItem(Item item) {
        this(item.id(), item.num());
    }

    public BagItem(int id, int num) {
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
        if (num < 0) {
            throw new IllegalArgumentException("num must be non-negative");
        }
        this.num = num;
    }

    public void addNum(int num) {
        if (num < 0) {
            throw new IllegalArgumentException("num must be non-negative");
        }
        this.num += num;
    }

    public void delNum(int num) {
        if (num < 0) {
            throw new IllegalArgumentException("num must be non-negative");
        }
        this.num -= num;
    }

    @Override
    protected BagItem clone() {
        try {
            return (BagItem) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
