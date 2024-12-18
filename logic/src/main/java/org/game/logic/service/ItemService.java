package org.game.logic.service;

import org.game.logic.entity.ItemInfo;
import org.game.logic.repository.ItemRepository;
import org.game.proto.struct.Login.PbRegister;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ItemService extends AbstractGameService<ItemInfo, ItemRepository> {

    @Override
    public void register(PbRegister registerMsg) {
        entity = new ItemInfo(player.getId());
    }

}
