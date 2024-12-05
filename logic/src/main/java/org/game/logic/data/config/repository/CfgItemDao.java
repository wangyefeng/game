package org.game.logic.data.config.repository;

import org.game.logic.data.config.entity.CfgItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CfgItemDao extends MongoRepository<CfgItem, Integer> {
}