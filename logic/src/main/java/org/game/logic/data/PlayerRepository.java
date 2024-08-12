package org.game.logic.data;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlayerRepository extends MongoRepository<PlayerInfo, Integer> {
}
