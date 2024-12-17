package org.game.logic.repository;

import org.game.logic.entity.ItemInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ItemRepository extends MongoRepository<ItemInfo, Integer> {
}
