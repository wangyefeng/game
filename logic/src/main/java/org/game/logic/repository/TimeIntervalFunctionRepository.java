package org.game.logic.repository;

import org.game.logic.entity.TimeIntervalFunctionInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TimeIntervalFunctionRepository extends MongoRepository<TimeIntervalFunctionInfo, Integer> {
}
