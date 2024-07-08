package org.wangyefeng.game.logic.net;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wangyefeng
 * @date 2024-07-08
 * @description 处理消息的handler
 */
@ChannelHandler.Sharable
public class GateHandler extends SimpleChannelInboundHandler<GateMessage<?>> {

    private static final Logger log = LoggerFactory.getLogger(GateHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GateMessage client2ServerMessage) {
        org.wangyefeng.game.logic.handler.GateHandler<Message> logicHandler = org.wangyefeng.game.logic.handler.GateHandler.getHandler(client2ServerMessage.getCode());
        if (logicHandler == null) {
            log.warn("illegal message code: {}", client2ServerMessage.getCode());
            return;
        }
        logicHandler.handle(ctx.channel(), client2ServerMessage.getMessage());
    }

}
