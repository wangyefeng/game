package org.game.logic.repository;

import org.game.logic.entity.TaskInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskRepository extends MongoRepository<TaskInfo, Integer> {
}
