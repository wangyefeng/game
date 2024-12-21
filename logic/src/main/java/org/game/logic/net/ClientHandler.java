package org.game.logic.net;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.game.config.Configs;
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
        log.info("client channel active: {}", ctx.channel().remoteAddress());
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
            long start = System.currentTimeMillis();
            try {
                logicHandler.handle(ctx.channel(), message.getPlayerId(), message.getMessage(), Configs.getInstance());
            } catch (Exception e) {
                log.error("处理玩家[{}]消息 协议：{} 数据: {} 出错", message.getPlayerId(), message.getProtocol(), message.getMessage(), e);
            } finally {
                long cost = System.currentTimeMillis() - start;
                if (cost > 1000) {
                    log.error("处理玩家[{}]消息 协议：{} 耗时：{}毫秒 数据: {}", message.getPlayerId(), message.getProtocol(), cost, message.getMessage());
                } else if (cost > 100) {
                    log.warn("处理玩家[{}]消息 协议：{} 耗时：{}毫秒 数据: {}", message.getPlayerId(), message.getProtocol(), cost, message.getMessage());
                } else if (cost > 50) {
                    log.info("处理玩家[{}]消息 协议：{} 耗时：{}毫秒 数据: {}", message.getPlayerId(), message.getProtocol(), cost, message.getMessage());
                } else {
                    log.debug("处理玩家[{}]消息 协议：{} 耗时：{}毫秒 数据: {}", message.getPlayerId(), message.getProtocol(), cost, message.getMessage());
                }
            }
        });
    }

}
