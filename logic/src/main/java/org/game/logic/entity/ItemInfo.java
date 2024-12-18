package org.game.logic.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document(collection = "item_info")
public class ItemInfo extends Entity {

    private Map<Integer, Item> items;

    public ItemInfo(int id) {
        super(id);
        this.items = new HashMap<>();
    }

    public Map<Integer, Item> getItems() {
        return items;
    }

    @Override
    public ItemInfo clone() {
        ItemInfo cloned = (ItemInfo) super.clone();
        cloned.items = new HashMap<>();
        for (Item item : items.values()) {
            cloned.items.put(item.getId(), item.clone());
        }
        return cloned;
    }
}
