package org.game.logic.player.item;

import org.game.config.entity.Item;

public interface Addable {

    void add(Item item);

    ItemType getType();
}
