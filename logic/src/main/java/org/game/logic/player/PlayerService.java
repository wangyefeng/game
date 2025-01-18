package org.game.logic.player;

import org.game.config.entity.Item;
import org.game.config.entity.PlayerEvent;
import org.game.logic.AbstractGameService;
import org.game.logic.entity.PlayerInfo;
import org.game.logic.player.item.Consumable;
import org.game.logic.player.item.ItemIdConstant;
import org.game.logic.player.item.ItemType;
import org.game.logic.repository.PlayerRepository;
import org.game.proto.struct.Login;
import org.game.proto.struct.Login.PbLoginResp;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PlayerService extends AbstractGameService<PlayerInfo, PlayerRepository> implements Consumable {


    @Override
    public void register(Login.PbRegisterReq registerMsg) {
        entity = new PlayerInfo(player.getId(), registerMsg.getName());
    }

    @Override
    public void loginResp(PbLoginResp.Builder loginResp) {
        loginResp.setId(entity.getPlayerId());
        loginResp.setLevel(entity.getLevel());
        loginResp.setName(entity.getName());
    }

    public boolean playerExists() {
        return repository.existsById(player.getId());
    }

    public void levelUp() {
        entity.setLevel(entity.getLevel() + 1);
        player.updateEvent(PlayerEvent.LEVEL_UP, entity.getLevel());
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
