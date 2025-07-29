package org.game.logic.repository;

import org.game.logic.entity.PlayerInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlayerRepository extends MongoRepository<PlayerInfo, Integer> {
}
