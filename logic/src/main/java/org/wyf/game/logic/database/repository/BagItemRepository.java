package org.wyf.game.logic.database.repository;

import org.wyf.game.logic.database.Repository;
import org.wyf.game.logic.database.entity.BagItem;

import java.util.Collection;

public interface BagItemRepository extends Repository<BagItem, BagItem.PK> {
    Collection<BagItem> findByPlayerId(int playerId);
}