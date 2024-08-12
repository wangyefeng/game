package org.game.gate.net.client;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.game.gate.handler.logic.LogicMsgHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.game.proto.MessageCode;

@ChannelHandler.Sharable
public class LogicHandler extends SimpleChannelInboundHandler<MessageCode<?>> {

    private static final Logger log = LoggerFactory.getLogger(LogicHandler.class);

    private LogicClient logicClient;

    public LogicHandler(LogicClient logicClient) {
        this.logicClient = logicClient;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageCode message) {
        LogicMsgHandler<Message> logicMsgHandler = LogicMsgHandler.getHandler(message.getCode());
        if (logicMsgHandler == null) {
            log.warn("illegal message code: {}", message.getCode());
            return;
        }
        logicMsgHandler.handle(ctx.channel(), message.getMessage());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logicClient.setRunning(false);
        logicClient.reconnect();
    }
}
