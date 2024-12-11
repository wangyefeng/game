package org.game.config.data.repository;

import org.game.config.data.entity.CfgItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CfgItemDao extends MongoRepository<CfgItem, Integer> {
}