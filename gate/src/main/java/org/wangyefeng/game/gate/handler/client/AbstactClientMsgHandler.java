package org.wangyefeng.game.gate.handler.client;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wangyefeng.game.gate.net.AttributeKeys;
import org.wangyefeng.game.gate.player.Player;

public abstract class AbstactClientMsgHandler<T extends Message> implements ClientMsgHandler<T> {

    private static final Logger log = LoggerFactory.getLogger(AbstactClientMsgHandler.class);

    @Override
    public void handle(Channel channel, T message) {
        // TODO 临时简单处理，后续需要优化逻辑线程
        Player player = channel.attr(AttributeKeys.PLAYER_ID).get();
        if (player == null) {
            log.warn("func={}, msg=playerId is null, channel={}", getClass().getSimpleName(), channel);
            return;
        }
        handle(channel, message, player);
    }

    protected abstract void handle(Channel channel, T message, Player player);
}
