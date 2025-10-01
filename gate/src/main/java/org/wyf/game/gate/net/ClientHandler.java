package org.wyf.game.gate.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.ReadTimeoutException;
import jakarta.annotation.Nonnull;
import org.wyf.game.gate.net.client.LogicHandler;
import org.wyf.game.gate.player.Player;
import org.wyf.game.gate.player.Players;
import org.wyf.game.proto.CodeMsgHandler;
import org.wyf.game.proto.MessageCode;
import org.wyf.game.proto.MsgHandler;
import org.wyf.game.proto.MsgHandlerFactory;
import org.wyf.game.proto.protocol.GateToLogicProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.SocketException;

@ChannelHandler.Sharable
@Component
public class ClientHandler extends SimpleChannelInboundHandler<MessageCode<?>> {

    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);

    private final MsgHandlerFactory msgHandlerFactory;

    @Autowired
    public ClientHandler(MsgHandlerFactory msgHandlerFactory) {
        this.msgHandlerFactory = msgHandlerFactory;
    }

    @Override
    public void channelActive(@Nonnull ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("客户端连接成功，channel:{}", ctx.channel());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void channelRead0(ChannelHandlerContext ctx, MessageCode<?> message) throws Exception {
        MsgHandler<?> handler = msgHandlerFactory.getHandler(message.protocol());
        if (handler == null) {
            log.error("illegal message code: {}", message.getCode());
            return;
        }
        if (!(handler instanceof CodeMsgHandler codeMsgHandler)) {
            log.error("illegal message code: {}", message.getCode());
            return;
        }
        codeMsgHandler.handle(ctx.channel(), message.data());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof SocketException) {
            log.info("Socket exception {} channel: {}", cause.getMessage(), ctx.channel().id());
        } else if (cause instanceof ReadTimeoutException) {
            log.info("Read timeout: {}", ctx.channel().id());
        } else {
            log.error("Exception caught in channel: {}", ctx.channel().id(), cause);
            ctx.close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel c = ctx.channel();
        log.info("客户端连接断开, 地址: {} channel: {}", c.remoteAddress(), c.id());
        Player player = c.attr(AttributeKeys.PLAYER).get();
        if (player != null) {
            player.getExecutor().submit(() -> {
                Players.removePlayer(player.getId());
                player.writeToLogic(GateToLogicProtocol.LOGOUT, player.getId());
                log.info("玩家退出游戏, 玩家ID: {} channel: {}", player.getId(), c.id());
                player.getLogicClient().getChannel().attr(LogicHandler.PLAYERS_KEY).get().remove((Integer) player.getId());
            }).get();
        }
    }

}
