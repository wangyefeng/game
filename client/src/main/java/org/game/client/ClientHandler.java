package org.game.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.game.proto.MessageCode;
import org.game.proto.protocol.ClientToGateProtocol;
import org.game.proto.protocol.ClientToLogicProtocol;
import org.game.proto.struct.Common;

import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<MessageCode<?>> {

    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);

    private final int playerId;

    public ClientHandler(int playerId) {
        this.playerId = playerId;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ctx.channel().writeAndFlush(new MessageCode<>(ClientToGateProtocol.VALIDATE, Common.PbInt.newBuilder().setVal(playerId).build()));
        ctx.channel().writeAndFlush(new MessageCode<>(ClientToLogicProtocol.LOGIN, Common.PbInt.newBuilder().setVal(11).build()));
        ctx.executor().scheduleAtFixedRate(() -> {
            log.info("ping");
            ctx.channel().writeAndFlush(new MessageCode<>(ClientToGateProtocol.PING));
        }, 5, 5, TimeUnit.SECONDS);
        ctx.executor().scheduleAtFixedRate(() -> {
            log.info("TEST");
            ctx.channel().writeAndFlush(new MessageCode<>(ClientToLogicProtocol.TEST, Common.PbInt.newBuilder().setVal(2).build()));
        }, 5, 5, TimeUnit.SECONDS);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageCode message) {
        log.info("Received message from {}: msg：{} content：{}", message.getProtocol().from(), message.getProtocol(), message.getMessage());
    }
}