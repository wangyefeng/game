package org.game.logic.net;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.game.config.Configs;
import org.game.logic.thread.ThreadPool;
import org.game.proto.MessagePlayer;
import org.game.proto.protocol.ClientToLogicProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.OperationNotSupportedException;

/**
 * @author wangyefeng
 * @date 2024-07-08
 * @description 处理消息的handler
 */
@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<ClientMessage<?>> {

    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClientMessage<?> clientMessage) throws OperationNotSupportedException {
        Object message = clientMessage.message();
        if (message instanceof MessagePlayer<?> messagePlayer) {
            ClientMsgHandler<Message> logicHandler = ClientMsgHandler.getHandler((ClientToLogicProtocol) messagePlayer.getProtocol());
            // 交给业务线程接管
            ThreadPool.getPlayerExecutor(messagePlayer.getPlayerId()).execute(() -> {
                long start = System.currentTimeMillis();
                try {
                    logicHandler.handle(ctx.channel(), messagePlayer.getPlayerId(), messagePlayer.getData(), Configs.getInstance());
                } catch (Exception e) {
                    log.error("协议处理失败：{}", messagePlayer, e);
                } finally {
                    long cost = System.currentTimeMillis() - start;
                    if (cost > 1000) {
                        log.error("处理协议耗时：{}毫秒 协议：{}", cost, messagePlayer);
                    } else if (cost > 100) {
                        log.warn("处理协议耗时：{}毫秒 协议：{}", cost, messagePlayer);
                    } else if (cost > 50) {
                        log.info("处理协议耗时：{}毫秒 协议：{}", cost, messagePlayer);
                    } else {
                        log.debug("处理协议耗时：{}毫秒 协议：{}", cost, messagePlayer);
                    }
                }
            });
        } else {
            throw new OperationNotSupportedException("不支持的消息类型");
        }
    }

}
