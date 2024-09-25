package org.game.logic.data.game;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlayerDao extends MongoRepository<PlayerInfo, Integer> {
}
