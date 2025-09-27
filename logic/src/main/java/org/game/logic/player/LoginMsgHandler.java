package org.game.logic.player;

import io.netty.channel.Channel;
import org.game.common.RedisKeys;
import org.game.logic.SpringConfig;
import org.game.logic.net.AbstractPlayerMsgHandler;
import org.game.logic.thread.ThreadPool;
import org.game.proto.protocol.ClientToLogicProtocol;
import org.game.proto.protocol.LogicToClientProtocol;
import org.game.proto.struct.Login;
import org.game.proto.struct.Login.PbLoginReq;
import org.game.proto.struct.Login.PbLoginResp;
import org.game.proto.struct.Login.PbLoginResp.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
public class LoginMsgHandler extends AbstractPlayerMsgHandler<PbLoginReq> {


    private static final Logger log = LoggerFactory.getLogger(LoginMsgHandler.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SpringConfig springConfig;

    @Override
    public void handle0(Channel channel, int playerId, Login.PbLoginReq data) {
        log.info("玩家{}登录游戏", playerId);
        Player p = Players.getPlayer(playerId);
        if (p != null) {
            p.execute(() -> login(channel, data, p, true));
        } else {
            Boolean success = redisTemplate.opsForHash().putIfAbsent(RedisKeys.PLAYER_INFO, String.valueOf(playerId), String.valueOf(springConfig.getLogicId()));
            if (!success) {
                log.warn("玩家{}在其他服务器上登录，拒绝登录", playerId);
                return;
            }
            ExecutorService playerExecutor = ThreadPool.getPlayerExecutor(playerId);
            Player player = new Player(playerId, applicationContext.getBeansOfType(GameService.class).values(), channel, playerExecutor);
            player.execute(() -> {
                PlayerService playerService = player.getService(PlayerService.class);
                if (!playerService.playerExists()) {
                    log.warn("玩家登录失败，玩家 {}不存在", playerId);
                    playerExecutor.close();
                    return;
                }
                Players.addPlayer(player);
                login(channel, data, player, false);
            });
        }
    }

    private void login(Channel channel, PbLoginReq data, Player player, boolean isReconnect) {
        player.login(data, channel, isReconnect);
        Builder resp = PbLoginResp.newBuilder();
        resp.setIsNew(false);
        player.loginResp(resp);
        player.writeToClient(LogicToClientProtocol.LOGIN, resp.build());
    }

    @Override
    public ClientToLogicProtocol getProtocol() {
        return ClientToLogicProtocol.LOGIN;
    }
}
