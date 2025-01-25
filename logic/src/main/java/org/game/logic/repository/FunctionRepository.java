package org.game.logic.repository;

import org.game.logic.entity.FunctionInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FunctionRepository extends MongoRepository<FunctionInfo, Integer> {
}
