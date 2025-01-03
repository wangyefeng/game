package org.game.config.service;

import org.game.config.Configs;
import org.game.config.ConfigException;
import org.game.config.entity.CfgFisheries;
import org.game.config.repository.CfgFisheriesDao;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("cfg_fisheries")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CfgFisheriesService extends CfgService<CfgFisheries, CfgFisheriesDao, String> {

    @Override
    protected void check0(CfgFisheries cfgFisheries, Configs config) throws ConfigException {

    }
}