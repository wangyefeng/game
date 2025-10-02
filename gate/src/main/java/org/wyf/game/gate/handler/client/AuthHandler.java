package org.wyf.game.gate.handler.client;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.netty.channel.Channel;
import org.wyf.game.common.RedisKeys;
import org.wyf.game.common.RedisKeys.Locks;
import org.wyf.game.common.util.TokenUtil;
import org.wyf.game.gate.net.AttributeKeys;
import org.wyf.game.gate.net.client.ClientGroup;
import org.wyf.game.gate.net.client.LogicClient;
import org.wyf.game.gate.net.client.LogicHandler;
import org.wyf.game.gate.player.Player;
import org.wyf.game.gate.player.Players;
import org.wyf.game.gate.thread.ThreadPool;
import org.wyf.game.gate.zookepper.ZookeeperProperties;
import org.wyf.game.proto.AbstractCodeMsgHandler;
import org.wyf.game.proto.MessageCode;
import org.wyf.game.proto.protocol.ClientToGateProtocol;
import org.wyf.game.proto.protocol.GateToClientProtocol;
import org.wyf.game.proto.struct.Login;
import org.wyf.game.proto.struct.Login.PbAuthReq;
import org.wyf.game.proto.struct.PlayerExistServiceGrpc;
import org.wyf.game.proto.struct.PlayerExistServiceGrpc.PlayerExistServiceBlockingStub;
import org.wyf.game.proto.struct.Rpc.PbPlayerExistReq;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public final class AuthHandler extends AbstractCodeMsgHandler<PbAuthReq> {

    private static final Logger log = LoggerFactory.getLogger(AuthHandler.class);

    @Autowired
    private ClientGroup<LogicClient> clientGroup;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ZookeeperProperties zookeeperProperties;

    /**
     * token有效期
     */
    private static final Duration TOKEN_TIMEOUT = Duration.ofDays(30);

    @Override
    public void handle(Channel channel, Login.PbAuthReq msg) throws Exception {
        if (channel.hasAttr(AttributeKeys.PLAYER)) {
            log.warn("player {} has already logged in.", channel.attr(AttributeKeys.PLAYER).get());
            return;
        }

        String token = msg.getToken();
        DecodedJWT d1 = TokenUtil.verify(token, TokenUtil.PLAYER_TOKEN_SECRET);
        if (d1.getIssuedAtAsInstant() == null) {
            log.warn("token{}认证失败，token中不包含创建时间信息！", token);
            channel.writeAndFlush(MessageCode.of(GateToClientProtocol.PLAYER_TOKEN_VALIDATE, Login.PbAuthResp.newBuilder().setSuccess(false).build()));
            return;
        }

        Claim claim = d1.getClaim("playerId");
        if (claim.isNull()) {
            log.warn("token{}认证失败，token中不包含playerId！", token);
            channel.writeAndFlush(MessageCode.of(GateToClientProtocol.PLAYER_TOKEN_VALIDATE, Login.PbAuthResp.newBuilder().setSuccess(false).build()));
            return;
        }
        int playerId = claim.asInt();
        String key = RedisKeys.PLAYER_TOKEN_PREFIX + playerId;
        String lockKey = Locks.TOKEN_LOCK_PREFIX + playerId;
        RLock rLock = redissonClient.getLock(lockKey);
        try {
            rLock.lock();
            String tokenInRedis = redisTemplate.opsForValue().get(key);
            if (tokenInRedis == null) {
                redisTemplate.opsForValue().set(key, token, TOKEN_TIMEOUT);
            } else if (!token.equals(tokenInRedis)) {
                DecodedJWT d2 = TokenUtil.verify(tokenInRedis, TokenUtil.PLAYER_TOKEN_SECRET);
                if (d1.getIssuedAt().after(d2.getIssuedAt())) {// 新生成的token，替换原token
                    redisTemplate.opsForValue().set(key, token, TOKEN_TIMEOUT);
                } else {
                    log.warn("玩家{}认证失败 token:{} 已失效", playerId, token);
                    channel.writeAndFlush(MessageCode.of(GateToClientProtocol.PLAYER_TOKEN_VALIDATE, Login.PbAuthResp.newBuilder().setSuccess(false).build()));
                    return;
                }
            }
        } finally {
            rLock.unlock();
        }

        ThreadPoolExecutor playerExecutor = ThreadPool.getPlayerExecutor(playerId);
        playerExecutor.submit(() -> {
            log.info("玩家{}认证成功 channel:{}", playerId, channel);
            Player player;
            boolean containsPlayer = Players.containsPlayer(playerId);
            Login.PbAuthResp.Builder resp = Login.PbAuthResp.newBuilder();
            resp.setPlayerId(playerId);
            if (containsPlayer) {// 顶号
                player = Players.getPlayer(playerId);
                player.writeToClient(GateToClientProtocol.KICK_OUT);
                player.getChannel().close();
                player.setChannel(channel);
            } else {
                if (clientGroup.getClients().isEmpty()) {
                    log.error("当前没有可用的logic服务器，请检查logic服务器是否正常启动！！！");
                    channel.writeAndFlush(MessageCode.of(GateToClientProtocol.PLAYER_TOKEN_VALIDATE, resp.setSuccess(false).build()));
                    channel.close();
                    return;
                } else {
                    // 检测redis中该玩家是否还在某个服务器缓存中
                    LogicClient logicClient;
                    String serverId = (String) redisTemplate.opsForHash().get(RedisKeys.PLAYER_INFO, String.valueOf(playerId));
                    if (serverId != null) {
                        logicClient = clientGroup.get(zookeeperProperties.rootPath() + "/" + serverId);
                        if (logicClient == null) {
                            logicClient = clientGroup.next();
                        }
                    } else {
                        logicClient = clientGroup.next();
                    }
                    player = new Player(playerId, channel, playerExecutor, logicClient);
                    Players.addPlayer(player);
                }
            }
            resp.setSuccess(true).setIsRegistered(playerExist(player));
            channel.attr(AttributeKeys.PLAYER).set(player);
            player.getLogicClient().getChannel().attr(LogicHandler.PLAYERS_KEY).get().add(player.getId());
            channel.writeAndFlush(MessageCode.of(GateToClientProtocol.PLAYER_TOKEN_VALIDATE, resp.build()));
        }).get();
    }

    private static boolean playerExist(Player player) {
        // 创建阻塞式存根
        PlayerExistServiceBlockingStub blockingStub = PlayerExistServiceGrpc.newBlockingStub(player.getLogicClient().getGrpcChannel());
        // 创建请求对象
        PbPlayerExistReq request = PbPlayerExistReq.newBuilder().setId(player.getId()).build();
        // 调用服务端方法并获取响应
        return blockingStub.exists(request).getExist();
    }

    @Override
    public ClientToGateProtocol getProtocol() {
        return ClientToGateProtocol.AUTH;
    }

}
