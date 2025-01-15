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
import org.game.proto.CodeMsgHandler;
import org.game.proto.MessageCode;
import org.game.proto.protocol.ClientToGateProtocol;
import org.game.proto.protocol.GateToClientProtocol;
import org.game.proto.struct.Login;
import org.game.proto.struct.Login.PbAuthReq;
import org.game.proto.struct.PlayerExistServiceGrpc;
import org.game.proto.struct.PlayerExistServiceGrpc.PlayerExistServiceBlockingStub;
import org.game.proto.struct.Rpc.PbPlayerExistReq;
import org.game.proto.struct.Rpc.PbPlayerExistResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

@Component
public final class AuthHandler implements CodeMsgHandler<PbAuthReq> {

    private static final Logger log = LoggerFactory.getLogger(AuthHandler.class);

    @Autowired
    private ClientGroup<LogicClient> clientGroup;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void handle(Channel channel, Login.PbAuthReq msg) throws Exception {
        if (channel.hasAttr(AttributeKeys.PLAYER)) {
            log.warn("player {} has already logged in.", channel.attr(AttributeKeys.PLAYER).get());
            return;
        }

        int playerId = msg.getId();
        String token = msg.getToken();
        if (!TokenUtil.verify(token, playerId, TokenUtil.PLAYER_TOKEN_SECRET)) {
            log.warn("玩家{}认证失败", playerId);
            channel.writeAndFlush(new MessageCode<>(GateToClientProtocol.PLAYER_TOKEN_VALIDATE, Login.PbAuthResp.newBuilder().setSuccess(false).build()));
            return;
        }
        String tokenInRedis = redisTemplate.opsForValue().get(RedisKeys.PLAYER_TOKEN_PREFIX + playerId);
        if (!token.equals(tokenInRedis)) {
            log.info("玩家{}认证失败,token已失效", playerId);
            channel.writeAndFlush(new MessageCode<>(GateToClientProtocol.PLAYER_TOKEN_VALIDATE, Login.PbAuthResp.newBuilder().setSuccess(false).build()));
            return;
        }
        ThreadPoolExecutor playerExecutor = ThreadPool.getPlayerExecutor(playerId);
        playerExecutor.submit(() -> {
            log.info("玩家{}认证成功 channel:{}", playerId, channel);
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
            // 创建阻塞式存根
            PlayerExistServiceBlockingStub blockingStub = PlayerExistServiceGrpc.newBlockingStub(player.getLogicClient().getGrpcChannel());
            // 创建请求对象
            PbPlayerExistReq request = PbPlayerExistReq.newBuilder().setId(playerId).build();
            // 调用服务端方法并获取响应
            PbPlayerExistResp response = blockingStub.exists(request);
            channel.writeAndFlush(new MessageCode<>(GateToClientProtocol.PLAYER_TOKEN_VALIDATE, Login.PbAuthResp.newBuilder().setSuccess(true).setId(playerId).setIsRegistered(response.getExist()).build()));
        }).get();
    }

    @Override
    public ClientToGateProtocol getProtocol() {
        return ClientToGateProtocol.AUTH;
    }

}
