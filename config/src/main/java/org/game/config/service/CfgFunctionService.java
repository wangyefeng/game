package org.game.config.service;

import org.game.config.entity.CfgFunction;
import org.game.config.repository.CfgFunctionRepository;
import org.springframework.stereotype.Service;

@Service
public class CfgFunctionService extends CfgService<CfgFunction, CfgFunctionRepository, Integer> {
}