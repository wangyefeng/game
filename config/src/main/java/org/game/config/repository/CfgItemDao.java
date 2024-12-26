package org.game.config.repository;

import org.game.config.entity.CfgItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CfgItemDao extends MongoRepository<CfgItem, Integer> {
}