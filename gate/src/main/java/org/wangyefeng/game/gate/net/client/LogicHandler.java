package org.wangyefeng.game.gate.net.client;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class LogicHandler extends SimpleChannelInboundHandler<LogicMessage<?>> {

    private static final Logger log = LoggerFactory.getLogger(LogicHandler.class);

    public LogicHandler() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogicMessage message) {
        org.wangyefeng.game.gate.handler.LogicHandler<Message> logicHandler = org.wangyefeng.game.gate.handler.LogicHandler.getHandler(message.getCode());
        if (logicHandler == null) {
            log.warn("illegal message code: {}", message.getCode());
            return;
        }
        logicHandler.handle(ctx.channel(), message.getMessage());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        LogicClient.getInstance().setRunning(false);
        LogicClient.getInstance().reconnect();
    }
}
