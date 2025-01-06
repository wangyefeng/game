package org.game.logic.net;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.game.config.Configs;
import org.game.logic.player.Player;
import org.game.logic.player.Players;
import org.game.logic.thread.ThreadPool;
import org.game.proto.MessageCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Vector;

/**
 * @author wangyefeng
 * @date 2024-07-08
 * @description 处理消息的handler
 */
@ChannelHandler.Sharable
public class GateHandler extends SimpleChannelInboundHandler<GateMessage<?>> {

    private static final Logger log = LoggerFactory.getLogger(GateHandler.class);

    public static final AttributeKey<List<Integer>> PLAYERS_KEY = AttributeKey.newInstance("players");

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ctx.channel().attr(PLAYERS_KEY).set(new Vector<>());
        log.info("client channel active: {}", ctx.channel().remoteAddress());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GateMessage clientMessage) {
        Object message = clientMessage.message();
        if (message instanceof MessageCode<?> messageCode) {
            GateMsgHandler.getHandler(messageCode.getProtocol()).handle(ctx.channel(), messageCode.getData(), Configs.getInstance());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("网络连接异常：", cause);
        if (ctx.channel().isActive()) {
            ctx.channel().close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        List<Integer> players = ctx.channel().attr(GateHandler.PLAYERS_KEY).get();
        for (Integer playerId : players) {
            if (Players.containsPlayer(playerId)) {
                ThreadPool.getPlayerExecutor(playerId).execute(() -> {
                    Player player = Players.getPlayer(playerId);
                    if (player == null) {
                        log.info("玩家{}退出游戏，但玩家不在线", playerId);
                        return;
                    }
                    player.logout();
                    Players.removePlayer(playerId);
                    log.info("玩家{}退出游戏", playerId);
                });
            }
        }
        log.info("与客户端连接断开, 地址：{}", ctx.channel().remoteAddress());
    }
}
