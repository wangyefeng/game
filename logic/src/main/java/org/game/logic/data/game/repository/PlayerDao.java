package org.game.logic.data.game.repository;

import org.game.logic.data.game.entity.PlayerInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerDao extends MongoRepository<PlayerInfo, Integer> {
}
