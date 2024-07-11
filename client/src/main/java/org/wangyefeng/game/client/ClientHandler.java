package org.wangyefeng.game.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.wangyefeng.game.proto.MessageCode;
import org.wangyefeng.game.proto.struct.Common;

import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<MessageCode<?>> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ctx.channel().writeAndFlush(new MessageCode<>(C2SProtocol.TOKEN_VALIDATE, Common.PbInt.newBuilder().setVal(100).build()));
        ctx.channel().writeAndFlush(new MessageCode<>(C2SProtocol.LOGIN, Common.PbInt.newBuilder().setVal(12).build()));
        ctx.executor().scheduleAtFixedRate(() -> ctx.channel().writeAndFlush(new MessageCode<>(C2SProtocol.PING)), 5, 5, TimeUnit.SECONDS);
        ctx.executor().scheduleAtFixedRate(() -> ctx.channel().writeAndFlush(new MessageCode<>(C2SProtocol.TEST)), 3, 3, TimeUnit.SECONDS);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageCode message) {
        System.out.println("Received message: " + message.getCode() + " " + message.getMessage());
    }
}
