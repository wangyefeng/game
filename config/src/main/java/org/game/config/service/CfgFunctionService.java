package org.game.config.service;

import org.game.config.Configs;
import org.game.config.entity.CfgFunction;
import org.game.config.repository.CfgFunctionRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CfgFunctionService extends CfgService<CfgFunction, CfgFunctionRepository, Integer> {

    @Override
    protected void check0(CfgFunction cfgFunction, Configs config) throws Exception {

    }
}
