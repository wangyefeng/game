package org.game.logic.repository;

import org.game.logic.entity.PlayerInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlayerDao extends MongoRepository<PlayerInfo, Integer> {
}
