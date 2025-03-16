package org.game.config.service;

import org.game.config.Configs;
import org.game.config.entity.CfgCyclicFunction;
import org.game.config.repository.CfgCyclicFunctionRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CfgCyclicFunctionService extends CfgService<CfgCyclicFunction, CfgCyclicFunctionRepository, Integer> {

    @Override
    protected void check0(CfgCyclicFunction cfgCyclicFunction, Configs config) throws Exception {
        Assert.isTrue(cfgCyclicFunction.getCycle() >= cfgCyclicFunction.getOpenDays(), "周期时间必须大于开启天数");
    }
}
