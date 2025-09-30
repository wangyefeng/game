package org.wyf.game.logic.player.item;

import java.util.HashMap;
import java.util.Map;

public enum ItemType {

    /**
     * 货币
     */
    CURRENCY(1),

    /**
     * 背包
     */
    BAG(2),
    ;

    private final int type;

    private static Map<Integer, ItemType> typeMap = new HashMap<>();

    static {
        for (ItemType itemType : ItemType.values()) {
            typeMap.put(itemType.type, itemType);
        }
    }

    ItemType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static ItemType getType(int type) {
        return typeMap.get(type);
    }
}
