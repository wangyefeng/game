package org.wyf.game.logic.database.entity;


import jakarta.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 背包
 */
@Entity
@Table
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BagInfo extends BaseInfo {

    /**
     * 道具
     */
    @Transient
    private Map<Integer, BagItem> items = new HashMap<>();

    /**
     * 背包容量
     */
    private int capacity;

    private BagInfo() {
        // for JPA
    }

    public BagInfo(int playerId, int capacity) {
        super(playerId);
        this.capacity = capacity;
    }

    public Map<Integer, BagItem> getItems() {
        return items;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public BagInfo clone() throws CloneNotSupportedException {
        BagInfo copy = (BagInfo) super.clone();
        copy.items = new HashMap<>();
        for (BagItem item : items.values()) {
            copy.items.put(item.getId(), item.clone());
        }
        return copy;
    }

    public void init(Collection<BagItem> bagItems) {
        for (BagItem bagItem : bagItems) {
            items.put(bagItem.getId(), bagItem);
        }
    }
}
