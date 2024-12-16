package org.game.logic.handler;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.game.config.Config;
import org.game.logic.player.Player;
import org.game.logic.player.Players;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽象玩家业务处理器
 *
 * @param <T> 协议体结构
 * @author wangyefeng
 * @date 2024-07-17
 */
public abstract class PlayerHandler<T extends Message> implements ClientMsgHandler<T> {


    private static final Logger log = LoggerFactory.getLogger(PlayerHandler.class);

    @Override
    public void handle(Channel channel, int playerId, T message, Config config) {
        Player player = Players.getPlayer(playerId);
        if (player == null) {
            log.warn("协议处理失败 玩家{}未登录，协议:{} 协议体：{}", playerId, getProtocol(), message);
            return;
        }
        try {
            long start = System.currentTimeMillis();
            handle(player, message, config);
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

    /**
     * 业务处理方法
     *
     * @param player  玩家对象
     * @param message 消息体内容
     */
    protected abstract void handle(Player player, T message, Config config);
}
