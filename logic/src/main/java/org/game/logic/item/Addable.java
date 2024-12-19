package org.game.logic.item;

import org.game.config.data.entity.Item;

public interface Addable {

    void add(Item item);

    ItemType getType();
}
