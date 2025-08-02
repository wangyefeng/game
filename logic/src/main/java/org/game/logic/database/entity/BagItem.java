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

    private long num;

    private BagItem() {
        // for JPA
    }

    public BagItem(int playerId, Item item) {
        this(playerId, item.id(), item.num());
    }

    public BagItem(int playerId, int itemId, long num) {
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

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        if (num < 0) {
            throw new IllegalArgumentException("num must be non-negative");
        }
        this.num = num;
    }

    public void addNum(long num) {
        if (num < 0) {
            throw new IllegalArgumentException("num must be non-negative");
        }
        this.num += num;
    }

    public void delNum(long num) {
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
    public long num() {
        return num;
    }

    public record PK(int playerId, int itemId) {
    }
}
