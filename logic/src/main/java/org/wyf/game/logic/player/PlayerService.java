package org.wyf.game.logic.player;

import org.wyf.game.common.RedisKeys;
import org.wyf.game.config.entity.Item;
import org.wyf.game.config.entity.PlayerEvent;
import org.wyf.game.logic.database.entity.PlayerInfo;
import org.wyf.game.logic.database.repository.PlayerRepository;
import org.wyf.game.logic.player.item.Consumable;
import org.wyf.game.logic.player.item.ItemIdConstant;
import org.wyf.game.logic.player.item.ItemType;
import org.wyf.game.proto.struct.Login;
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

    @Override
    public void register(Login.PbRegisterReq registerMsg) {
        entity = new PlayerInfo(player.getId(), registerMsg.getName());
    }

    @Override
    public void loginResp(Login.PbLoginOrRegisterResp.Builder loginResp) {
        loginResp.setLevel(entity.getLevel());
        loginResp.setName(entity.getName());
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
        Long delete = redisTemplate.opsForHash().delete(RedisKeys.PLAYER_INFO, String.valueOf(playerId));
        if (delete == 0) {
            log.error("删除玩家信息失败！ redis中没有该玩家服务器信息, playerId:{}", playerId);
        }
    }
}
