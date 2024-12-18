package org.game.logic.service;

import org.game.common.event.Listener;
import org.game.common.event.Publisher;
import org.game.logic.entity.Item;
import org.game.logic.entity.ItemInfo;
import org.game.logic.player.PlayerEventType;
import org.game.logic.repository.ItemRepository;
import org.game.proto.struct.Login.PbRegister;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ItemService extends AbstractGameService<ItemInfo, ItemRepository> {

    @Override
    public void register(PbRegister registerMsg) {
        entity = new ItemInfo(player.getId());
    }

    public void addItem(Item item) {
        Map<Integer, Item> items = entity.getItems();
        Item it = items.get(item.getId());
        if (it != null) {
            it.addNum(item.getNum());
        } else {
            items.put(item.getId(), item);
        }
    }

    public void initListener() {
        player.addEventListener(PlayerEventType.LEVEL_UP, new Listener<Integer>() {

            @Override
            public void update(Integer level, Publisher<Integer> publisher) {
                if (level == 2) {
                    addItem(new Item(1, 200));
                }
                if (level == 4) {
                    addItem(new Item(2, 500));
                }
            }
        });
    }
}
