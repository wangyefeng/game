package org.game.logic.net;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.game.config.Configs;
import org.game.logic.thread.ThreadPool;
import org.game.proto.PlayerMsgHandler;
import org.game.proto.protocol.ClientToLogicProtocol;
import org.game.proto.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ClientMsgHandler<T extends Message> implements PlayerMsgHandler<T> {

    private static final Logger log = LoggerFactory.getLogger(ClientMsgHandler.class);

    @Override
    public void handle(Channel channel, int playerId, T data) {
        ThreadPool.getPlayerExecutor(playerId).execute(() -> {
            long start = System.currentTimeMillis();
            try {
                handle0(channel, playerId, data, Configs.getInstance());
            } catch (Exception e) {
                log.error("协议处理失败：{}", data, e);
            } finally {
                long cost = System.currentTimeMillis() - start; if (cost > 1000) {
                    log.error("处理协议耗时：{}毫秒 协议：{}", cost, Protocol.toString(getProtocol()));
                } else if (cost > 100) {
                    log.warn("处理协议耗时：{}毫秒 协议：{}", cost, Protocol.toString(getProtocol()));
                } else if (cost > 50) {
                    log.info("处理协议耗时：{}毫秒 协议：{}", cost, Protocol.toString(getProtocol()));
                } else {
                    log.debug("处理协议耗时：{}毫秒 协议：{}", cost, Protocol.toString(getProtocol()));
                }
            }
        });
    }

    protected abstract void handle0(Channel channel, int playerId, T data, Configs config);

    @Override
    public abstract ClientToLogicProtocol getProtocol();
}
