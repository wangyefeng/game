package org.wangyefeng.game.logic.net;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wangyefeng.game.logic.handler.GateMsgHandler;
import org.wangyefeng.game.proto.MessageCode;

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

}
