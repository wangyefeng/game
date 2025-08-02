package org.game.logic.player.item;

import org.game.config.entity.Item;

public interface Addable {

    void add(int itemId, long num);

    ItemType getType();
}
