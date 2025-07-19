package org.game.config.service;

import org.game.config.ConfigException;
import org.game.config.Configs;
import org.game.config.entity.CfgCyclicFunction;
import org.game.config.repository.CfgCyclicFunctionRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CfgCyclicFunctionService extends CfgService<CfgCyclicFunction, CfgCyclicFunctionRepository, Integer> {
}
