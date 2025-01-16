package org.game.config.service;

import org.game.config.entity.CfgFunction;
import org.game.config.repository.CfgFunctionRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CfgFunctionService extends CfgService<CfgFunction, CfgFunctionRepository, Integer> {
}