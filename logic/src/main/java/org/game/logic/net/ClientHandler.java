package org.game.logic.net;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.game.logic.handler.ClientMsgHandler;
import org.game.logic.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.game.proto.MessagePlayer;

/**
 * @author wangyefeng
 * @date 2024-07-08
 * @description 处理消息的handler
 */
@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<MessagePlayer<?>> {

    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("client channel active: {}", ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessagePlayer<?> message) {
        ClientMsgHandler<Message> logicHandler = ClientMsgHandler.getHandler(message.getCode());
        if (logicHandler == null) {
            log.error("illegal message code: {}", message.getCode());
            return;
        }
        // 交给业务线程接管
        ThreadPool.getPlayerExecutor(message.getPlayerId()).execute(() -> {
            try {
                logicHandler.handle(ctx.channel(), message.getPlayerId(), message.getMessage());
            } catch (Exception e) {
                log.error("handle message error", e);
            }
        });
    }

}
