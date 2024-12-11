package org.game.logic.data.repository;

import org.game.logic.data.entity.PlayerInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlayerDao extends MongoRepository<PlayerInfo, Integer> {
}
