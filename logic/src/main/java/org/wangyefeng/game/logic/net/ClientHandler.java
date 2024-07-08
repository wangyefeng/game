package org.wangyefeng.game.logic.net;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wangyefeng.game.logic.handler.PlayerHandler;

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
        PlayerHandler<Message> logicHandler = PlayerHandler.getHandler(clientMessage.getCode());
        if (logicHandler == null) {
            log.warn("illegal message code: {}", clientMessage.getCode());
            return;
        }
        logicHandler.handle(ctx.channel(), clientMessage.getPlayerId(), clientMessage.getMessage());
    }

}
