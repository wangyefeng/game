package org.game.gate.net.client;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.game.common.Server;
import org.game.gate.handler.logic.LogicMsgHandler;
import org.game.gate.player.Player;
import org.game.gate.player.Players;
import org.game.gate.thread.ThreadPool;
import org.game.proto.MessageCode;
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
    protected void channelRead0(ChannelHandlerContext ctx, MessageCode message) {
        LogicMsgHandler<Message> logicMsgHandler = LogicMsgHandler.getHandler(message.getCode());
        if (logicMsgHandler == null) {
            log.warn("illegal message code: {}", message.getCode());
            return;
        }
        try {
            logicMsgHandler.handle(ctx.channel(), message.getData());
        } catch (Exception e) {
            log.error("处理logic消息{}时发生异常", message, e);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
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
