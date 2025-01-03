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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

@Component
public final class PlayerValidateHandler implements ClientMsgHandler<Login.PbPlayerValidateReq> {

    private static final Logger log = LoggerFactory.getLogger(PlayerValidateHandler.class);

    @Autowired
    private ClientGroup<LogicClient> clientGroup;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void handle(Channel channel, Login.PbPlayerValidateReq msg) throws Exception {
        if (channel.hasAttr(AttributeKeys.PLAYER)) {
            log.warn("player {} has already logged in.", channel.attr(AttributeKeys.PLAYER).get());
            return;
        }

        int playerId = msg.getId();
        String token = msg.getToken();
        if (!TokenUtil.verify(token, playerId, TokenUtil.PLAYER_TOKEN_SECRET)) {
            log.warn("player {} token verify failed.", playerId);
            channel.writeAndFlush(new MessageCode<>(GateToClientProtocol.PLAYER_TOKEN_VALIDATE, Login.PbPlayerValidateResp.newBuilder().setSuccess(false).build()));
            return;
        }
        String tokenInRedis = redisTemplate.opsForValue().get(RedisKeys.PLAYER_TOKEN_PREFIX + playerId);
        if (!token.equals(tokenInRedis)) {
            log.info("player {} token verify failed.", playerId);
            channel.writeAndFlush(new MessageCode<>(GateToClientProtocol.PLAYER_TOKEN_VALIDATE, Login.PbPlayerValidateResp.newBuilder().setSuccess(false).build()));
            return;
        }
        ThreadPoolExecutor playerExecutor = ThreadPool.getPlayerExecutor(playerId);
        playerExecutor.submit(() -> {
            log.info("Player {} is logging in. channel: {}", playerId, channel.id());
            Player player;
            boolean containsPlayer = Players.containsPlayer(playerId);
            if (containsPlayer) {// 顶号
                player = Players.getPlayer(playerId);
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
            Boolean isRegistered = redisTemplate.opsForSet().isMember(RedisKeys.ALL_PLAYERS, playerId + "");
            channel.writeAndFlush(new MessageCode<>(GateToClientProtocol.PLAYER_TOKEN_VALIDATE, Login.PbPlayerValidateResp.newBuilder().setSuccess(true).setId(playerId).setIsRegistered(isRegistered).build()));
        }).get();
    }

    @Override
    public ClientToGateProtocol getProtocol() {
        return ClientToGateProtocol.PLAYER_VALIDATE;
    }

}
