package org.game.logic.net;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.game.logic.handler.GateMsgHandler;
import org.game.proto.MessageCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;

/**
 * @author wangyefeng
 * @date 2024-07-08
 * @description 处理消息的handler
 */
@ChannelHandler.Sharable
public class GateHandler extends SimpleChannelInboundHandler<MessageCode<?>> {

    private static final Logger log = LoggerFactory.getLogger(GateHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageCode<?> message) {
        GateMsgHandler<Message> logicHandler = GateMsgHandler.getHandler(message.getProtocol().getCode());
        if (logicHandler == null) {
            log.warn("illegal message code: {}", message.getProtocol());
            return;
        }
        logicHandler.handle(ctx.channel(), message.getMessage());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof SocketException && "Connection reset".equals(cause.getMessage())) {
            log.info("Connection reset");
        } else {
            log.error("gate处理器发生异常：", cause);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.info("客户端断开连接, 地址：{}", ctx.channel().remoteAddress());
    }
}
