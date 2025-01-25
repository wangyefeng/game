package org.game.logic.repository;

import org.game.logic.entity.ActivityInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActivityRepository extends MongoRepository<ActivityInfo, Integer> {
}
