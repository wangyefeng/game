package org.game.config.data.service;

import org.game.config.data.entity.CfgFisheries;
import org.game.config.data.repository.CfgFisheriesDao;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CfgFisheriesService extends CfgService<CfgFisheries, CfgFisheriesDao, String> {
}