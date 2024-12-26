package org.game.logic.bag;

import org.game.config.entity.Item;
import org.game.logic.AbstractGameService;
import org.game.logic.item.Consumable;
import org.game.logic.item.ItemType;
import org.game.proto.struct.Login.PbRegister;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BagService extends AbstractGameService<BagInfo, BagRepository> implements Consumable {

    @Override
    public void register(PbRegister registerMsg) {
        entity = new BagInfo(player.getId());
    }

    @Override
    public boolean enough(Item item) {
        if (item.num() == 0) {
            return true;
        }
        Map<Integer, BagItem> items = entity.getItems();
        BagItem bagItem = items.get(item.id());
        if (bagItem == null) {
            return false;
        }
        return bagItem.getNum() >= item.num();
    }

    @Override
    public void consume(Item item) {
        if (!enough(item)) {
            throw new RuntimeException("背包物品不足，消耗失败！！！id = " + item.id());
        }
        entity.getItems().get(item.id()).delNum(item.num());
    }

    @Override
    public void add(Item item) {
        Map<Integer, BagItem> items = entity.getItems();
        BagItem it = items.get(item.id());
        if (it != null) {
            it.addNum(item.num());
        } else {
            items.put(item.id(), new BagItem(item));
        }
    }

    @Override
    public ItemType getType() {
        return ItemType.BAG;
    }

}
