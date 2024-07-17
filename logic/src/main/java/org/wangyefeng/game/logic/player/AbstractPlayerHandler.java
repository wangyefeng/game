package org.wangyefeng.game.logic.player;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wangyefeng.game.logic.data.Player;
import org.wangyefeng.game.logic.handler.ClientMsgHandler;

public abstract class AbstractPlayerHandler<T extends Message> implements ClientMsgHandler<T> {


    private static final Logger log = LoggerFactory.getLogger(AbstractPlayerHandler.class);

    @Override
    public void handle(Channel channel, int playerId, T message) {
        Player player = Players.getPlayer(playerId);
        if (player == null) {
            log.warn("协议处理失败 玩家{}未登录，协议:{} 协议体：{}", playerId, getProtocol(), message);
            return;
        }
        try {
            long start = System.currentTimeMillis();
            handle(player, message);
            long end = System.currentTimeMillis();
            long costTime = end - start;
            // 打印日志
            if (costTime < 10) {
                log.debug("处理消息成功 玩家id:{} 消息号:{} 协议体：{} 耗时:{}ms", playerId, getProtocol(), message, costTime);
            } else if (costTime < 30) {
                log.warn("处理消息成功 玩家id:{} 消息号:{} 协议体：{} 耗时:{}ms", playerId, getProtocol(), message, costTime);
            } else {
                log.error("处理消息成功 玩家id:{} 消息号:{} 协议体：{} 耗时:{}ms", playerId, getProtocol(), message, costTime);
            }
        } catch (Exception e) {
            log.error("处理消息失败 玩家id:{} 消息号:{} 协议体：{}", playerId, getProtocol(), message, e);
        }
    }

    protected abstract void handle(Player player, T message);
}
