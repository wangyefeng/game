package org.wangyefeng.game.logic.net;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wangyefeng.game.logic.handler.ClientMsgHandler;

/**
 * @author wangyefeng
 * @date 2024-07-08
 * @description 处理消息的handler
 */
@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<ClientMessage<?>> {

    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("client channel active: {}", ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClientMessage clientMessage) {
        ClientMsgHandler<Message> logicHandler = ClientMsgHandler.getHandler(clientMessage.getCode());
        if (logicHandler == null) {
            log.warn("illegal message code: {}", clientMessage.getCode());
            return;
        }
        logicHandler.handle(ctx.channel(), clientMessage.getPlayerId(), clientMessage.getMessage());
    }

}
