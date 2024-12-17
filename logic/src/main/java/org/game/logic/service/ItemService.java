package org.game.logic.service;

import org.game.common.util.JsonUtil;
import org.game.logic.entity.Item;
import org.game.logic.entity.ItemInfo;
import org.game.logic.repository.ItemRepository;
import org.game.proto.struct.Login.PbRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ItemService extends AbGameService {

    @Autowired
    private ItemRepository itemRepository;

    private ItemInfo itemInfo;

    @Override
    public void load() {
        itemInfo = itemRepository.findById(player.getId()).orElseThrow();
    }

    @Override
    public void save() {
        itemRepository.save(itemInfo);
    }

    @Override
    public void init(PbRegister registerMsg) {
        itemInfo = new ItemInfo(player.getId());
        itemInfo.getItems().add(new Item(1, 100));
    }

    @Override
    public String dataToString() {
        return JsonUtil.toJson(itemInfo);
    }
}
