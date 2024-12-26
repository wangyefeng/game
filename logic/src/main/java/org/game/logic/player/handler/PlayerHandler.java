package org.game.logic.player.handler;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.game.config.Configs;
import org.game.logic.net.ClientMsgHandler;
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
    public void handle(Channel channel, int playerId, T message, Configs config) {
        Player player = Players.getPlayer(playerId);
        if (player == null) {
            log.warn("协议处理失败 玩家{}未登录，协议:{} 数据：{}", playerId, getProtocol(), message);
            return;
        }
        handle(player, message, config);
    }

    /**
     * 业务处理方法
     *
     * @param player  玩家对象
     * @param message 消息体内容
     */
    protected abstract void handle(Player player, T message, Configs config);
}
