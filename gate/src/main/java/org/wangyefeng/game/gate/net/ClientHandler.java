package org.wangyefeng.game.gate.net;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.ReadTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wangyefeng.game.gate.handler.client.ClientMsgHandler;
import org.wangyefeng.game.gate.player.Player;
import org.wangyefeng.game.gate.player.Players;

import java.net.SocketException;

@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<ClientMessage<?>> {

    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("Channel active: {}", ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClientMessage message) {
        ClientMsgHandler<Message> handler = ClientMsgHandler.getHandler(message.getCode());
        if (handler == null) {
            log.warn("illegal message code: {}", message.getCode());
            return;
        }
        handler.handle(ctx.channel(), message.getMessage());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof SocketException) {
            log.info("Socket exception {} channel: {}", cause.getMessage(), ctx.channel());
        } else if (cause instanceof ReadTimeoutException) {
            log.info("Read timeout: {}", ctx.channel());
        } else {
            log.error("Exception caught in channel: {}", ctx.channel(), cause);
            ctx.close();
        }
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel c = ctx.channel();
        log.info("Channel inactive: {}", c);
        if (c.hasAttr(AttributeKeys.PLAYER)) {
            Player player = c.attr(AttributeKeys.PLAYER).get();
            Players.lock.lock();
            try {
                Players.removePlayer(player.getId());
            } finally {
                Players.lock.unlock();
            }
        }
    }

}
