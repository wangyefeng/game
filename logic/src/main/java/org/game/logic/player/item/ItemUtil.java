package org.game.logic.player.item;

import org.game.config.entity.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class ItemUtil {

    public static <T extends Item> Collection<AddableItem> mergeItems(Collection<T> items) {
        Map<Integer, AddableItem> result = new HashMap<>();
        for (Item item : items) {
            result.computeIfAbsent(item.id(), _ -> new AddableItem(item.id(), item.num()));
            result.get(item.id()).add(item.num());
        }
        return result.values();
    }

    public static Collection<AddableItem> mergeItems(Item[] items) {
        Map<Integer, AddableItem> result = new HashMap<>();
        for (Item item : items) {
            result.computeIfAbsent(item.id(), _ -> new AddableItem(item.id(), item.num()));
            result.get(item.id()).add(item.num());
        }
        return result.values();
    }
}
