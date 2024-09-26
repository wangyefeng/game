package org.game.logic.data.config;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CfgItemDao extends MongoRepository<CfgItem, Integer> {
}
