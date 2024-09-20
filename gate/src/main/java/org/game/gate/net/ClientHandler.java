package org.game.gate.net;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.ReadTimeoutException;
import org.game.gate.player.Player;
import org.game.gate.player.Players;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.game.gate.handler.client.ClientMsgHandler;
import org.game.gate.net.client.LogicClient;
import org.game.proto.MessageCode;
import org.game.proto.protocol.GateToLogicProtocol;
import org.game.proto.struct.Common;

import java.net.SocketException;

@ChannelHandler.Sharable
@Component
public class ClientHandler extends SimpleChannelInboundHandler<MessageCode<?>> {

    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);

    @Autowired
    private LogicClient logicClient;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("与客户端建立TCP连接，地址：{} id: {}", ctx.channel().remoteAddress(), ctx.channel().id());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageCode message) throws Exception {
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
        log.info("与客户端断开TCP连接, 地址: {} channel: {}", c.remoteAddress(), c.id());
        Player player = c.attr(AttributeKeys.PLAYER).get();
        if (player != null) {
            player.getExecutor().submit(() -> {
                Player player2 = c.attr(AttributeKeys.PLAYER).get();
                Players.removePlayer(player2.getId());
                if (logicClient.isRunning()) {
                    logicClient.getChannel().writeAndFlush(new MessageCode<>(GateToLogicProtocol.LOGOUT, Common.PbInt.newBuilder().setVal(player2.getId()).build()));
                }
            }).get();
        }
    }

}
