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
public class ClientHandler extends SimpleChannelInboundHandler<ClientMessage<?>> {

    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClientMessage clientMessage) {
        org.wangyefeng.game.logic.handler.ClientHandler<Message> logicHandler = org.wangyefeng.game.logic.handler.ClientHandler.getHandler(clientMessage.getCode());
        if (logicHandler == null) {
            log.warn("illegal message code: {}", clientMessage.getCode());
            return;
        }
        logicHandler.handle(ctx.channel(), clientMessage.getPlayerId(), clientMessage.getMessage());
    }

}
