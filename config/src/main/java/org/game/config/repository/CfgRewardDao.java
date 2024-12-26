package org.game.config.repository;

import org.game.config.entity.CfgReward;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CfgRewardDao extends MongoRepository<CfgReward, Integer> {
}