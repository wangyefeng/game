package org.game.test.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CfgItemDao extends MongoRepository<CfgItem, Integer> {
}