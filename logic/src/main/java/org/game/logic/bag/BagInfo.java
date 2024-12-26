package org.game.logic.bag;

import org.game.logic.Entity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * 背包
 */
@Document(collection = "bag_info")
public class BagInfo extends Entity {

    /**
     * 道具
     */
    private Map<Integer, BagItem> items;

    public BagInfo(int id) {
        super(id);
        this.items = new HashMap<>();
    }

    public Map<Integer, BagItem> getItems() {
        return items;
    }

    @Override
    public BagInfo clone() {
        BagInfo copy = (BagInfo) super.clone();
        copy.items = new HashMap<>();
        for (BagItem item : items.values()) {
            copy.items.put(item.getId(), item.clone());
        }
        return copy;
    }
}
