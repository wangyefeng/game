package org.game.logic.item;

public interface Consumable extends Addable {

    boolean enough(Item item);

    void consume(Item item);
}
