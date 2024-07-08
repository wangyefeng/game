package org.wangyefeng.game.logic.handler;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.wangyefeng.game.logic.protocol.Gate2LogicProtocol;

import java.util.HashMap;
import java.util.Map;

public interface PlayerHandler<T extends Message> {

    Map<Integer, PlayerHandler<Message>> handlers = new HashMap<>();

    static void register(Handler<? extends Message> handler) {
        if (handlers.containsKey(handler.getProtocol().getCode())) {
            throw new IllegalArgumentException("Duplicate protocol:" + handler.getProtocol());
        }
        handlers.put(handler.getProtocol().getCode(), (PlayerHandler<Message>) handler);
    }

    static PlayerHandler<Message> getHandler(int code) {
        return handlers.get(code);
    }

    void handle(Channel channel, int playerId, T message);

    Gate2LogicProtocol getProtocol();
}
