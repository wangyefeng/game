package org.wangyefeng.game.gate.handler.client;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wangyefeng.game.gate.net.AttributeKeys;
import org.wangyefeng.game.gate.player.Player;

public abstract class AbstractPlayerMsgHandler<T extends Message> implements ClientMsgHandler<T> {

    private static final Logger log = LoggerFactory.getLogger(AbstractPlayerMsgHandler.class);

    @Override
    public void handle(Channel channel, T message) {
        Player player = channel.attr(AttributeKeys.PLAYER).get();
        if (player == null) {
            log.warn("func={}, msg=playerId is null, channel={}", getClass().getSimpleName(), channel);
            return;
        }
        player.getExecutor().execute(() -> handle(channel, message, player));
    }

    protected abstract void handle(Channel channel, T message, Player player);
}
