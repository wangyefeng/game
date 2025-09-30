package org.wyf.game.logic.player;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.wyf.game.common.RedisKeys;
import org.wyf.game.logic.LogicConfig;
import org.wyf.game.logic.database.repository.PlayerRepository;
import org.wyf.game.logic.net.AbstractPlayerMsgHandler;
import org.wyf.game.logic.thread.ThreadPool;
import org.wyf.game.proto.protocol.ClientToLogicProtocol;
import org.wyf.game.proto.protocol.LogicToClientProtocol;
import org.wyf.game.proto.struct.Login;
import org.wyf.game.proto.struct.Login.PbRegisterReq;

import java.util.concurrent.ExecutorService;

@Component
public class RegisterMsgHandler extends AbstractPlayerMsgHandler<PbRegisterReq> {


    private static final Logger log = LoggerFactory.getLogger(RegisterMsgHandler.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private LogicConfig logicConfig;

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public void handle(Channel channel, int playerId, Login.PbRegisterReq data) {
        log.info("玩家{}注册 信息: {}", playerId, data);
        if (Players.containsPlayer(playerId)) {
            log.warn("玩家{}已经在内存中，不能重复注册", playerId);
            return;
        }
        ExecutorService playerDBExecutor = ThreadPool.getPlayerDBExecutor(playerId);
        playerDBExecutor.execute(() -> {
            if (playerRepository.existsById(playerId)) {
                log.warn("玩家{}已经存在数据库中，不能重复注册", playerId);
                return;
            }
            Boolean success = redisTemplate.opsForHash().putIfAbsent(RedisKeys.PLAYER_INFO, String.valueOf(playerId), String.valueOf(logicConfig.serverId()));
            if (!success) {
                log.warn("玩家{}在其他服务器上注册，拒绝注册", playerId);
                return;
            }
            Player player = new Player(playerId, applicationContext.getBeansOfType(GameService.class).values(), channel);
            player.execute(() -> {
                Players.addPlayer(player);
                player.register(data);
                Login.PbLoginOrRegisterResp.Builder resp = Login.PbLoginOrRegisterResp.newBuilder();
                resp.setIsNew(true);
                player.loginOrRegisterResp(resp);
                player.writeToClient(LogicToClientProtocol.LOGIN, resp.build());
            });
        });
    }

    @Override
    public ClientToLogicProtocol getProtocol() {
        return ClientToLogicProtocol.REGISTER;
    }
}
