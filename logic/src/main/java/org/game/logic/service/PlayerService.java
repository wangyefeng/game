package org.game.logic.service;

import org.game.logic.entity.PlayerInfo;
import org.game.logic.item.Consumable;
import org.game.logic.item.Item;
import org.game.logic.item.ItemIdConstant;
import org.game.logic.item.ItemType;
import org.game.logic.player.PlayerEventType;
import org.game.logic.repository.PlayerRepository;
import org.game.proto.struct.Login;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PlayerService extends AbstractGameService<PlayerInfo, PlayerRepository> implements Consumable {

    @Override
    public void register(Login.PbRegister registerMsg) {
        entity = new PlayerInfo(player.getId(), registerMsg.getName());
    }

    public boolean playerExists() {
        return repository.existsById(player.getId());
    }

    public void levelUp() {
        entity.setLevel(entity.getLevel() + 1);
        player.updateEvent(PlayerEventType.LEVEL_UP, entity.getLevel());
    }

    @Override
    public boolean enough(Item item) {
        int id = item.id();
        switch (id) {
            case ItemIdConstant.COIN -> {
                return entity.getCoin() >= item.num();
            }
            default -> throw new IllegalArgumentException("Invalid item id: " + id);
        }
    }

    @Override
    public void consume(Item item) {
        int id = item.id();
        switch (id) {
            case ItemIdConstant.COIN -> entity.setCoin(entity.getCoin() - item.num());
            default -> throw new IllegalArgumentException("Invalid item id: " + id);
        }
    }

    @Override
    public void add(Item item) {
        int id = item.id();
        switch (id) {
            case ItemIdConstant.COIN -> entity.setCoin(entity.getCoin() + item.num());
            default -> throw new IllegalArgumentException("Invalid item id: " + id);
        }
    }

    @Override
    public ItemType getType() {
        return ItemType.CURRENCY;
    }
}
