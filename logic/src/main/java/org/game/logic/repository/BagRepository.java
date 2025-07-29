package org.game.logic.repository;

import org.game.logic.entity.BagInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BagRepository extends MongoRepository<BagInfo, Integer> {
}
