package org.game.config.data.service;

import org.game.config.Configs;
import org.game.config.data.entity.CfgFisheries;
import org.game.config.data.repository.CfgFisheriesDao;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("cfg_fisheries")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CfgFisheriesService extends CfgService<CfgFisheries, CfgFisheriesDao, String> {

    @Override
    public void check(Configs config) throws Exception {
        super.check(config);
    }
}