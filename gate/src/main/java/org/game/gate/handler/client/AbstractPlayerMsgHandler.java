package org.game.gate.handler.client;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.game.gate.net.AttributeKeys;
import org.game.gate.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPlayerMsgHandler<T extends Message> implements ClientMsgHandler<T> {

    private static final Logger log = LoggerFactory.getLogger(AbstractPlayerMsgHandler.class);

    @Override
    public void handle(Channel channel, T message) {
        Player player = channel.attr(AttributeKeys.PLAYER).get();
        if (player == null) {
            log.warn("func={}, msg=playerId is null, channel={}", getClass().getSimpleName(), channel);
            return;
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
