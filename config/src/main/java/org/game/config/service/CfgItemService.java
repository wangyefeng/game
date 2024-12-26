package org.game.config.service;

import org.game.config.Configs;
import org.game.config.ConfigException;
import org.game.config.entity.CfgItem;
import org.game.config.repository.CfgItemDao;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("config_item")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CfgItemService extends CfgService<CfgItem, CfgItemDao, Integer> {

    @Override
    protected void check0(CfgItem cfgItem, Configs config) throws ConfigException {

    }
}