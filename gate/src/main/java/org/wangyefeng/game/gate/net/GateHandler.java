package org.wangyefeng.game.gate.net;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wangyefeng.game.gate.handler.Handler;

@ChannelHandler.Sharable
public class GateHandler extends SimpleChannelInboundHandler<GateMessage<?>> {

    private static final Logger log = LoggerFactory.getLogger(GateHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GateMessage message) {
        Handler<Message> handler = Handler.getHandler(message.getCode());
        if (handler == null) {
            log.warn("illegal message code: {}", message.getCode());
            return;
        }
        handler.handle(ctx.channel(), message.getMessage());
    }

}
