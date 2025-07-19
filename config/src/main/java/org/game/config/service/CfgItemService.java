package org.game.config.service;

import org.game.config.entity.CfgItem;
import org.game.config.entity.Item;
import org.game.config.repository.CfgItemRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CfgItemService extends CfgService<CfgItem, CfgItemRepository, Integer> {

    public boolean itemExists(Collection<Item> cfgItems) {
        return cfgItems.stream().anyMatch(cfgItem -> exists(cfgItem.id()));
    }

    public boolean itemExists(Item... cfgItems) {
        return Arrays.stream(cfgItems).anyMatch(cfgItem -> exists(cfgItem.id()));
    }
}
