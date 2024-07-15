package org.wangyefeng.game.gate.handler.client;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wangyefeng.game.gate.net.AttributeKeys;
import org.wangyefeng.game.gate.player.Player;

public abstract class AbstractPlayerMsgHandler<T extends Message> implements ClientMsgHandler<T> {

    private static final Logger log = LoggerFactory.getLogger(AbstractPlayerMsgHandler.class);

    /**
     * 最大消息队列长度
     */
    private static final int MAX_QUEUE_SIZE = 20;

    @Override
    public void handle(Channel channel, T message) {
        Player player = channel.attr(AttributeKeys.PLAYER).get();
        if (player == null) {
            log.warn("func={}, msg=playerId is null, channel={}", getClass().getSimpleName(), channel);
            return;
        }
        if (player.getExecutor().getQueue().size() > MAX_QUEUE_SIZE) {
            throw new IllegalStateException("处理玩家消息异常，消息队列已满 playerId: " + player.getId());
        }
        player.getExecutor().execute(() -> {
            Player player2 = channel.attr(AttributeKeys.PLAYER).get();
            if (player2 != null) {
                try {
                    handle(channel, message, player2);
                } catch (Exception e) {
                    log.error("func={}, msg=处理玩家消息异常, playerId={}, channel={}", getClass().getSimpleName(), player.getId(), channel, e);
                }
            }
        });
    }

    protected abstract void handle(Channel channel, T message, Player player) throws Exception;
}
