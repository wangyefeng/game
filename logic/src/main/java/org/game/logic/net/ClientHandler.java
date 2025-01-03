package org.game.logic.net;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.game.config.Configs;
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
    protected void channelRead0(ChannelHandlerContext ctx, MessagePlayer<?> message) {
        ClientMsgHandler<Message> logicHandler = ClientMsgHandler.getHandler(message.getProtocol());
        if (logicHandler == null) {
            log.warn("非法协议: {} {}", message.getProtocol().getClass().getSimpleName(), message.getProtocol());
            return;
        }
        // 交给业务线程接管
        ThreadPool.getPlayerExecutor(message.getPlayerId()).execute(() -> {
            long start = System.currentTimeMillis();
            try {
                logicHandler.handle(ctx.channel(), message.getPlayerId(), message.getData(), Configs.getInstance());
            } catch (Exception e) {
                log.error("处理玩家[{}]消息 协议：{} 数据: {} 出错", message.getPlayerId(), message.getProtocol(), message.getData(), e);
            } finally {
                long cost = System.currentTimeMillis() - start;
                if (cost > 1000) {
                    log.error("处理玩家[{}]消息 耗时：{}毫秒 协议：{} 数据: {}", message.getPlayerId(), cost, message.getProtocol(), message.getData());
                } else if (cost > 100) {
                    log.warn("处理玩家[{}]消息 耗时：{}毫秒 协议：{} 数据: {}", message.getPlayerId(), cost, message.getProtocol(), message.getData());
                } else if (cost > 50) {
                    log.info("处理玩家[{}]消息 耗时：{}毫秒 协议：{} 数据: {}", message.getPlayerId(), cost, message.getProtocol(), message.getData());
                } else {
                    log.debug("处理玩家[{}]消息 耗时：{}毫秒 协议：{} 数据: {}", message.getPlayerId(), cost, message.getProtocol(), message.getData());
                }
            }
        });
    }

}
