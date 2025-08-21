package org.game.logic.player;

import akka.actor.typed.ActorRef;
import io.netty.channel.Channel;
import org.game.common.RedisKeys;
import org.game.logic.SpringConfig;
import org.game.logic.actor.Action;
import org.game.logic.actor.PlayerAction;
import org.game.logic.actor.PlayerActorService;
import org.game.logic.net.AbstractPlayerMsgHandler;
import org.game.proto.protocol.ClientToLogicProtocol;
import org.game.proto.protocol.LogicToClientProtocol;
import org.game.proto.struct.Login;
import org.game.proto.struct.Login.PbLoginResp;
import org.game.proto.struct.Login.PbLoginResp.Builder;
import org.game.proto.struct.Login.PbRegisterReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RegisterMsgHandler extends AbstractPlayerMsgHandler<PbRegisterReq> {


    private static final Logger log = LoggerFactory.getLogger(RegisterMsgHandler.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PlayerActorService playerActorService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SpringConfig springConfig;

    @Autowired
    private PlayerService playerService;

    @Override
    public void handle0(Channel channel, int playerId, Login.PbRegisterReq data) {
        log.info("玩家{}注册 信息: {}", playerId, data);
        Boolean success = redisTemplate.opsForHash().putIfAbsent(RedisKeys.PLAYER_INFO, String.valueOf(playerId), String.valueOf(springConfig.getLogicId()));
        if (!success) {
            log.warn("玩家{}在其他服务器上注册，拒绝注册", playerId);
            return;
        }
        if (Players.containsPlayer(playerId)) {
            log.info("玩家{}已经在内存中，不能重复注册", playerId);
            redisTemplate.opsForHash().delete(RedisKeys.PLAYER_INFO, String.valueOf(playerId));
            return;
        }
        if (playerService.playerExists()) {
            log.info("玩家{}已经存在数据库中，不能重复注册", playerId);
            redisTemplate.opsForHash().delete(RedisKeys.PLAYER_INFO, String.valueOf(playerId));
            return;
        }
        ActorRef<Action> playerActor = playerActorService.createActor(playerId);
        playerActor.tell((PlayerAction) (() -> {
            Player player = new Player(playerId, applicationContext.getBeansOfType(GameService.class).values(), channel, playerActor);
            player.register(data);
            Players.addPlayer(player);
            Builder resp = PbLoginResp.newBuilder();
            resp.setIsNew(true);
            player.loginResp(resp);
            player.writeToClient(LogicToClientProtocol.LOGIN, resp.build());
        }));
    }

    @Override
    public ClientToLogicProtocol getProtocol() {
        return ClientToLogicProtocol.REGISTER;
    }
}
