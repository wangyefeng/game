package org.wyf.game.gate.net.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import jakarta.annotation.Nonnull;
import org.wyf.game.gate.player.Player;
import org.wyf.game.gate.player.Players;
import org.wyf.game.gate.thread.ThreadPool;
import org.wyf.game.proto.CodeMsgHandler;
import org.wyf.game.proto.MessageCode;
import org.wyf.game.proto.MsgHandler;
import org.wyf.game.proto.MsgHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Vector;

@ChannelHandler.Sharable
public class LogicHandler extends SimpleChannelInboundHandler<MessageCode<?>> {

    private static final Logger log = LoggerFactory.getLogger(LogicHandler.class);

    public static final AttributeKey<List<Integer>> PLAYERS_KEY = AttributeKey.newInstance("players");

    private final MsgHandlerFactory msgHandlerFactory;

    private final LogicClient logicClient;

    public LogicHandler(LogicClient logicClient, MsgHandlerFactory msgHandlerFactory) {
        this.msgHandlerFactory = msgHandlerFactory;
        this.logicClient = logicClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ctx.channel().attr(PLAYERS_KEY).set(new Vector<>());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageCode message) throws Exception {
        MsgHandler handler = msgHandlerFactory.getHandler(message.protocol());
        if (handler == null) {
            log.warn("illegal message : {}", message);
            return;
        }
        if (handler instanceof CodeMsgHandler codeMsgHandler) {
            codeMsgHandler.handle(ctx.channel(), message.data());
        } else {
            log.error("illegal handler : {}", handler.getClass().getSimpleName());
        }
    }

    @Override
    public void channelInactive(@Nonnull ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.info("与逻辑服务器连接断开！{}", ctx.channel().remoteAddress());
        List<Integer> players = ctx.channel().attr(PLAYERS_KEY).get();
        for (Integer playerId : players) {
            ThreadPool.getPlayerExecutor(playerId).execute(() -> {
                Player player = Players.getPlayer(playerId);
                if (player != null) {
                    player.getChannel().close();
                }
            });
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exception caught", cause);
    }
}
