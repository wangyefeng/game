package org.game.logic.player;

import org.game.common.RedisKeys;
import org.game.config.entity.Item;
import org.game.config.entity.PlayerEvent;
import org.game.logic.Logic;
import org.game.logic.database.entity.PlayerInfo;
import org.game.logic.database.repository.PlayerRepository;
import org.game.logic.player.item.Consumable;
import org.game.logic.player.item.ItemIdConstant;
import org.game.logic.player.item.ItemType;
import org.game.proto.struct.Login;
import org.game.proto.struct.Login.PbLoginResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PlayerService extends AbstractGameService<PlayerInfo, PlayerRepository> implements Consumable {

    private static final Logger log = LoggerFactory.getLogger(PlayerService.class);
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private Logic logic;

    @Override
    public void register(Login.PbRegisterReq registerMsg) {
        entity = new PlayerInfo(player.getId(), registerMsg.getName());
    }

    @Override
    public void loginResp(PbLoginResp.Builder loginResp) {
        loginResp.setLevel(entity.getLevel());
        loginResp.setName(entity.getName());
    }

    public boolean playerExists() {
        return repository.existsById(player.getId());
    }

    public void levelUp() {
        entity.setLevel(entity.getLevel() + 1);
        player.updateEvent(PlayerEvent.LEVEL_UP, entity.getLevel());
        if (entity.getLevel() % 5 == 0) {
            player.addItem(10, 100);
            player.addItem(1, 1000);
        }
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
        if (!enough(item)) {
            throw new IllegalArgumentException("Not enough item, id:" + item.id() + " num:" + item.num());
        }
        int id = item.id();
        switch (id) {
            case ItemIdConstant.COIN -> entity.setCoin(entity.getCoin() - item.num());
            default -> throw new IllegalArgumentException("Invalid item id: " + id);
        }
    }

    @Override
    public void add(int itemId, long num) {
        switch (itemId) {
            case ItemIdConstant.COIN -> entity.setCoin(entity.getCoin() + num);
            default -> throw new IllegalArgumentException("Invalid item id: " + itemId);
        }
    }

    @Override
    public ItemType getType() {
        return ItemType.CURRENCY;
    }

    public void destroy() {
        int playerId = getEntity().getPlayerId();
        Long delete = redisTemplate.opsForHash().delete(RedisKeys.PLAYER_LOGIC, String.valueOf(playerId));
        if (delete == 0) {
            log.error("删除玩家信息失败！ redis中没有该玩家服务器信息, playerId:{}", playerId);
        }
    }
}
