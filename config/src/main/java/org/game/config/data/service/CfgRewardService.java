package org.game.config.data.service;

import org.game.config.data.entity.CfgReward;
import org.game.config.data.repository.CfgRewardDao;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CfgRewardService extends CfgService<CfgReward, CfgRewardDao, Integer> {
}