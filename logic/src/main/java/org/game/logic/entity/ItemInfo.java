package org.game.logic.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "item_info")
public class ItemInfo extends Entity {

    private List<Item> items;

    public ItemInfo(int id) {
        super(id);
        this.items = new ArrayList<>();
    }

    public List<Item> getItems() {
        return items;
    }

    @Override
    public ItemInfo clone() {
        ItemInfo cloned = (ItemInfo) super.clone();
        cloned.items = new ArrayList<>();
        for (Item item : items) {
            cloned.items.add(item.clone());
        }
        return cloned;
    }
}
