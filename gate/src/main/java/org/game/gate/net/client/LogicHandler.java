package org.game.gate.net.client;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.game.gate.Gate;
import org.game.gate.handler.logic.LogicMsgHandler;
import org.game.proto.MessageCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        if (Gate.isStopping()) {
            log.info("与逻辑服务器连接断开！{}", ctx.channel().remoteAddress());
        } else {
            log.error("与逻辑服务器连接断开！{}", ctx.channel().remoteAddress());
        }
        super.channelInactive(ctx);
        logicClient.setRunning(false);
        if (!Gate.isStopping()) {
            log.info("尝试重新连接逻辑服务器{}...", ctx.channel().remoteAddress());
            logicClient.reconnect();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exception caught", cause);
    }
}
