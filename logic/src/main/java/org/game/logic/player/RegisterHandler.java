package org.game.logic.player;

import io.netty.channel.Channel;
import org.game.common.RedisKeys;
import org.game.config.Configs;
import org.game.logic.GameService;
import org.game.logic.entity.PlayerInfo;
import org.game.logic.net.ClientMsgHandler;
import org.game.proto.protocol.ClientToLogicProtocol;
import org.game.proto.protocol.LogicToClientProtocol;
import org.game.proto.struct.Login;
import org.game.proto.struct.Login.PbRegisterReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RegisterHandler implements ClientMsgHandler<PbRegisterReq> {


    private static final Logger log = LoggerFactory.getLogger(RegisterHandler.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void handle(Channel channel, int playerId, Login.PbRegisterReq message, Configs config) {
        log.info("玩家{}注册 信息: {}", playerId, message);
        Player player = Players.getPlayer(playerId);
        if (player != null) {
            return;
        }
        player = new Player(playerId, applicationContext.getBeansOfType(GameService.class).values(), channel);
        PlayerService playerService = player.getService(PlayerService.class);
        if (playerService.playerExists()) {
            return;
        }
        player.register(message);
        redisTemplate.opsForSet().add(RedisKeys.ALL_PLAYERS, playerId + "");
        Players.addPlayer(player);
        PlayerInfo playerInfo = playerService.getEntity();
        player.sendToClient(LogicToClientProtocol.REGISTER, Login.PbLoginResp.newBuilder().setId(playerId).setName(playerInfo.getName()).setLevel(playerInfo.getLevel()).build());
    }

    @Override
    public ClientToLogicProtocol getProtocol() {
        return ClientToLogicProtocol.REGISTER;
    }
}
