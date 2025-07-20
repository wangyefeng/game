package org.game.logic.player.bag;

import org.game.config.entity.Item;
import org.game.logic.database.entity.BagInfo;
import org.game.logic.database.entity.BagItem;
import org.game.logic.database.repository.BagItemRepository;
import org.game.logic.database.repository.BagRepository;
import org.game.logic.player.AbstractGameService;
import org.game.logic.player.item.Consumable;
import org.game.logic.player.item.ItemType;
import org.game.proto.struct.Login.PbLoginResp.Builder;
import org.game.proto.struct.Login.PbRegisterReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BagService extends AbstractGameService<BagInfo, BagRepository> implements Consumable {

    @Autowired
    private BagItemRepository bagItemRepository;

    @Override
    public void load() {
        super.load();
        entity.init(bagItemRepository.findByPlayerId(player.getId()));
    }

    @Override
    protected void save(BagInfo entity, boolean cacheEvict) {
        super.save(entity, cacheEvict);
        for (BagItem item : entity.getItems().values()) {
            bagItemRepository.save(item, cacheEvict);
        }
    }

    @Override
    public void register(PbRegisterReq registerMsg) {
        entity = new BagInfo(player.getId(), 10);
    }

    @Override
    public void loginResp(Builder loginResp) {
        // do nothing
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
            items.put(item.id(), new BagItem(player.getId(), item));
        }
    }

    @Override
    public ItemType getType() {
        return ItemType.BAG;
    }

}
