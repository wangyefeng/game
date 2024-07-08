package org.wangyefeng.game.logic.player;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wangyefeng.game.logic.handler.ClientHandler;

public abstract class AbstractPlayerHandler<T extends Message> implements ClientHandler<T> {


    private static final Logger log = LoggerFactory.getLogger(AbstractPlayerHandler.class);

    @Override
    public void handle(Channel channel, int playerId, T message) {
        try {
            long start = System.currentTimeMillis();
            handle(new Player(playerId), message);
            long end = System.currentTimeMillis();
            if (end - start > 10) {// 超过10ms就打印日志
                log.warn("处理消息成功 玩家id:{} 消息号:{} 协议体：{} 耗时:{}ms", playerId, getProtocol(), message, end - start);
            }
        } catch (Exception e) {
            log.error("处理消息失败 玩家id:{} 消息号:{} 协议体：{}", playerId, getProtocol(), message, e);
        }
    }

    protected abstract void handle(Player player, T message) throws Exception;
}
