package org.game.logic.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "item_info")
public class ItemInfo extends Entity {

    @Id
    private int playerId;

    private List<Item> items;

    public ItemInfo(int playerId) {
        this.playerId = playerId;
        this.items = new ArrayList<>();
    }

    public int getPlayerId() {
        return playerId;
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
