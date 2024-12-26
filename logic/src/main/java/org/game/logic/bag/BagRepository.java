package org.game.logic.bag;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface BagRepository extends MongoRepository<BagInfo, Integer> {
}
