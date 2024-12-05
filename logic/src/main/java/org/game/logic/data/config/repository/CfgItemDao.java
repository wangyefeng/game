package org.game.logic.data.config.repository;

import org.game.logic.data.config.entity.CfgItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CfgItemDao extends MongoRepository<CfgItem, Integer> {
}