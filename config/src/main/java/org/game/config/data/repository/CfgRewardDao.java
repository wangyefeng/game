package org.game.config.data.repository;

import org.game.config.data.entity.CfgReward;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CfgRewardDao extends MongoRepository<CfgReward, Integer> {
}