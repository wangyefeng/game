package org.game.config.data.service;

import org.game.config.Configs;
import org.game.config.data.entity.CfgActivity;
import org.game.config.data.repository.CfgActivityDao;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("cfg_activity")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CfgActivityService extends CfgService<CfgActivity, CfgActivityDao, String> {

    @Override
    public void check0(Configs config) throws Exception {
    }
}
