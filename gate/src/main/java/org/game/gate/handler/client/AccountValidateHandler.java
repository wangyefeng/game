package org.game.gate.handler.client;

import io.netty.channel.Channel;
import org.game.common.RedisKeys;
import org.game.common.util.TokenUtil;
import org.game.gate.net.AttributeKeys;
import org.game.gate.net.client.ClientGroup;
import org.game.gate.net.client.LogicClient;
import org.game.gate.net.client.LogicHandler;
import org.game.gate.player.Player;
import org.game.gate.player.Players;
import org.game.gate.thread.ThreadPool;
import org.game.proto.MessageCode;
import org.game.proto.protocol.ClientToGateProtocol;
import org.game.proto.protocol.GateToClientProtocol;
import org.game.proto.struct.Login;
import org.game.proto.struct.Login.PbAccountValidateResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.management.timer.Timer;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public final class AccountValidateHandler implements ClientMsgHandler<Login.PbAccountValidateReq> {

    private static final Logger log = LoggerFactory.getLogger(AccountValidateHandler.class);

    @Autowired
    private ClientGroup<LogicClient> clientGroup;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void handle(Channel channel, Login.PbAccountValidateReq msg) throws Exception {
        if (channel.hasAttr(AttributeKeys.PLAYER)) {
            log.warn("player {} has already logged in.", channel.attr(AttributeKeys.PLAYER).get());
            return;
        }

        int playerId = msg.getId();
        String token = msg.getToken();
        if (!TokenUtil.verify(token, playerId, TokenUtil.TOKEN_SECRET)) {
            log.info("玩家{}身份验证失败。 channel: {}", playerId, channel);
            channel.writeAndFlush(new MessageCode<>(GateToClientProtocol.ACCOUNT_TOKEN_VALIDATE, Login.PbAccountValidateResp.newBuilder().setSuccess(false).build()));
            return;
        }
        ThreadPoolExecutor playerExecutor = ThreadPool.getPlayerExecutor(playerId);
        playerExecutor.submit(() -> {
            String ps = playerId + "";
            String playerToken = TokenUtil.token(playerId, TokenUtil.PLAYER_TOKEN_SECRET, new Date(System.currentTimeMillis() + Timer.ONE_DAY * 30));
            redisTemplate.opsForValue().set(RedisKeys.PLAYER_TOKEN_PREFIX + playerId, playerToken, 30, TimeUnit.DAYS);
            log.info("玩家{}身份验证成功。 channel: {}", playerId, channel);
            Player player;
            boolean containsPlayer = Players.containsPlayer(playerId);
            if (containsPlayer) {// 顶号
                player = Players.getPlayer(playerId);
                log.info("玩家{}被顶号 channel：{}", playerId, player.getChannel());
                player.writeToClient(GateToClientProtocol.KICK_OUT);
                player.getChannel().close();
                player.setChannel(channel);
            } else {
                if (clientGroup.getClients().isEmpty()) {
                    channel.writeAndFlush(new MessageCode<>(GateToClientProtocol.KICK_OUT));
                    channel.close();
                    return;
                }
                player = new Player(playerId, channel, playerExecutor, clientGroup.next());
                Players.addPlayer(player);
            }
            channel.attr(AttributeKeys.PLAYER).set(player);
            player.getLogicClient().getChannel().attr(LogicHandler.PLAYERS_KEY).get().add(player.getId());
            Boolean isRegistered = redisTemplate.opsForSet().isMember(RedisKeys.ALL_PLAYERS, ps);
            channel.writeAndFlush(new MessageCode<>(GateToClientProtocol.ACCOUNT_TOKEN_VALIDATE, PbAccountValidateResp.newBuilder().setSuccess(true).setId(playerId).setPlayerToken(playerToken).setIsRegistered(isRegistered).build()));
        }).get();
    }

    @Override
    public ClientToGateProtocol getProtocol() {
        return ClientToGateProtocol.ACCOUNT_VALIDATE;
    }

}
