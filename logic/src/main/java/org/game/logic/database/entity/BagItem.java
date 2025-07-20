package org.game.logic.database.entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import org.game.config.entity.Item;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 背包中的物品
 */
@Entity
@IdClass(BagItem.PK.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BagItem implements Cloneable, Item {

    @Id
    private int playerId;

    @Id
    @Column(name = "item_id", columnDefinition = "INT COMMENT '物品id'")
    private int itemId;

    private int num;

    private BagItem() {
        // for JPA
    }

    public BagItem(int playerId, Item item) {
        this(playerId, item.id(), item.num());
    }

    public BagItem(int playerId, int itemId, int num) {
        if (num < 0) {
            throw new IllegalArgumentException("num must be non-negative");
        }
        this.itemId = itemId;
        this.num = num;
        this.playerId = playerId;
    }

    public int getId() {
        return itemId;
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
    protected BagItem clone() throws CloneNotSupportedException {
        return (BagItem) super.clone();
    }

    @Override
    public int id() {
        return itemId;
    }

    @Override
    public int num() {
        return num;
    }

    public record PK(int playerId, int itemId) {
    }
}
