package org.wyf.game.logic.player.item;

public interface Addable {

    void add(int itemId, long num);

    ItemType getType();
}
