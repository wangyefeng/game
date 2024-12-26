package org.game.config.service;

import org.game.config.Configs;
import org.game.config.ConfigException;
import org.game.config.entity.CfgReward;
import org.game.config.repository.CfgRewardDao;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("config_reward")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CfgRewardService extends CfgService<CfgReward, CfgRewardDao, Integer> {

    @Override
    protected void check0(CfgReward cfgReward, Configs config) throws ConfigException {

    }
}