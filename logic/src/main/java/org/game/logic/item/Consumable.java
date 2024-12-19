package org.game.logic.item;

import org.game.config.data.entity.Item;

public interface Consumable extends Addable {

    boolean enough(Item item);

    void consume(Item item);
}
