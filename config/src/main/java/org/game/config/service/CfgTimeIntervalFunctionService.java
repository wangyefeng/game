package org.game.config.service;

import org.game.config.Configs;
import org.game.config.entity.CfgTimeIntervalFunction;
import org.game.config.repository.CfgTimeIntervalFunctionRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CfgTimeIntervalFunctionService extends CfgService<CfgTimeIntervalFunction, CfgTimeIntervalFunctionRepository, Integer> {

    @Override
    protected void check0(CfgTimeIntervalFunction cfgFunction, Configs config) throws Exception {
        System.out.println();
    }

    @Override
    protected void init() {
        super.init();
    }
}
