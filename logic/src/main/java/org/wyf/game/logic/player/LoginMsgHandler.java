package org.wyf.game.logic.player;

import com.google.protobuf.Int32Value;
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
import org.wyf.game.proto.MessageCode;
import org.wyf.game.proto.protocol.ClientToLogicProtocol;
import org.wyf.game.proto.protocol.LogicToClientProtocol;
import org.wyf.game.proto.protocol.LogicToGateProtocol;
import org.wyf.game.proto.struct.Login;
import org.wyf.game.proto.struct.Login.PbLoginReq;

import java.util.concurrent.ExecutorService;

@Component
public class LoginMsgHandler extends AbstractPlayerMsgHandler<PbLoginReq> {


    private static final Logger log = LoggerFactory.getLogger(LoginMsgHandler.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private LogicConfig logicConfig;

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public void handle(Channel channel, int playerId, Login.PbLoginReq data) {
        log.info("玩家{}登录游戏", playerId);
        Player p = Players.getPlayer(playerId);
        if (p != null) {
            if (p.getChannel() != null && p.getChannel() != channel) {// 顶号
                log.info("玩家{}已在其他地方登录，踢下线 新连接={} 旧连接={}", playerId, channel, p.getChannel());
                p.getChannel().writeAndFlush(MessageCode.of(LogicToGateProtocol.KICK_OUT, Int32Value.of(playerId)));
            }
            p.execute(() -> login(channel, data, p, true), getProtocol(), data);
        } else {
            ExecutorService playerDBExecutor = ThreadPool.getPlayerDBExecutor(playerId);
            playerDBExecutor.execute(() -> {
                if (!playerRepository.existsById(playerId)) {
                    log.warn("玩家登录失败，玩家 {}不存在", playerId);
                    return;
                }
                Boolean success = redisTemplate.opsForHash().putIfAbsent(RedisKeys.PLAYER_INFO, String.valueOf(playerId), String.valueOf(logicConfig.getServerId()));
                if (!success) {
                    log.warn("玩家{}在其他服务器上登录，拒绝登录", playerId);
                    return;
                }
                Player player = new Player(playerId, applicationContext.getBeansOfType(GameService.class).values(), channel);
                player.loadFromDb();
                player.execute(() -> {
                    Players.addPlayer(player);
                    login(channel, data, player, false);
                });
            });
        }
    }

    private void login(Channel channel, PbLoginReq data, Player player, boolean isReconnect) {
        player.login(data, channel, isReconnect);
        Login.PbLoginOrRegisterResp.Builder resp = Login.PbLoginOrRegisterResp.newBuilder();
        resp.setIsNew(false);
        player.loginOrRegisterResp(resp);
        player.writeToClient(LogicToClientProtocol.LOGIN, resp.build());
    }

    @Override
    public ClientToLogicProtocol getProtocol() {
        return ClientToLogicProtocol.LOGIN;
    }
}
