package org.game.config.data.service;

import org.game.config.data.entity.CfgItem;
import org.game.config.data.repository.CfgItemDao;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CfgItemService extends CfgService<CfgItem, CfgItemDao, Integer> {

    public void add(CfgItem cfgItem) {
        repository.save(cfgItem);
    }
}