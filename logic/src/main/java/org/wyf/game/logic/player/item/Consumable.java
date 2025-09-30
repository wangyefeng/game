package org.wyf.game.logic.player.item;

import org.wyf.game.config.entity.Item;

public interface Consumable extends Addable {

    boolean enough(Item item);

    void consume(Item item);
}
