package org.wangyefeng.game.logic.net;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wangyefeng.game.logic.handler.Handler;

/**
 * @author wangyefeng
 * @date 2024-07-08
 * @description 处理消息的handler
 */
@ChannelHandler.Sharable
public class Gate2ServerHandler extends SimpleChannelInboundHandler<Gate2ServerMessage<?>> {

    private static final Logger log = LoggerFactory.getLogger(Gate2ServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Gate2ServerMessage client2ServerMessage) {
        Handler<Message> logicHandler = Handler.getHandler(client2ServerMessage.getCode());
        if (logicHandler == null) {
            log.warn("illegal message code: {}", client2ServerMessage.getCode());
            return;
        }
        logicHandler.handle(ctx.channel(), client2ServerMessage.getMessage());
    }

}
