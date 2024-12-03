package org.game.logic.data.config;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CfgItemDao extends MongoRepository<CfgItem, Integer> {

    @Override
    @Cacheable(value = "cfg", key = "'cfg_item'")
    List<CfgItem> findAll();
}
