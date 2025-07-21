package org.game.gate.net.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import jakarta.annotation.Nonnull;
import org.game.common.Server;
import org.game.gate.player.Player;
import org.game.gate.player.Players;
import org.game.gate.thread.ThreadPool;
import org.game.proto.CodeMsgHandler;
import org.game.proto.MessageCode;
import org.game.proto.MsgHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Vector;

@ChannelHandler.Sharable
public class LogicHandler extends SimpleChannelInboundHandler<MessageCode<?>> {

    private static final Logger log = LoggerFactory.getLogger(LogicHandler.class);

    private LogicClient logicClient;

    public static final AttributeKey<List<Integer>> PLAYERS_KEY = AttributeKey.newInstance("players");

    public LogicHandler(LogicClient logicClient) {
        this.logicClient = logicClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ctx.channel().attr(PLAYERS_KEY).set(new Vector<>());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageCode message) throws Exception {
        MsgHandler handler = MsgHandler.getHandler(message.getProtocol());
        if (handler == null) {
            log.warn("illegal message : {}", message);
            return;
        }
        if (handler instanceof CodeMsgHandler codeMsgHandler) {
            codeMsgHandler.handle(ctx.channel(), message.getData());
        } else {
            log.error("illegal handler : {}", handler.getClass().getSimpleName());
        }
    }

    @Override
    public void channelInactive(@Nonnull ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.info("与逻辑服务器连接断开！{}", ctx.channel().remoteAddress());
        logicClient.setRunning(false);
        List<Integer> players = ctx.channel().attr(PLAYERS_KEY).get();
        for (Integer playerId : players) {
            ThreadPool.getPlayerExecutor(playerId).execute(() -> {
                Player player = Players.getPlayer(playerId);
                if (player != null) {
                    player.getChannel().close();
                }
            });
        }
        if (!Server.isStopping()) {
            log.info("尝试重新连接逻辑服务器{}...", ctx.channel().remoteAddress());
            logicClient.reconnect();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exception caught", cause);
    }
}
