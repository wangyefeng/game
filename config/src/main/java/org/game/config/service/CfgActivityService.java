package org.game.config.service;

import org.game.config.ConfigException;
import org.game.config.Configs;
import org.game.config.entity.CfgActivity;
import org.game.config.repository.CfgActivityRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CfgActivityService extends CfgService<CfgActivity, CfgActivityRepository, Integer> {

    @Override
    protected void check0(CfgActivity cfgActivity, Configs config) throws ConfigException {

    }
}
