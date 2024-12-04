package org.game.logic.handler;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.game.logic.data.mongodb.config.Config;
import org.game.proto.protocol.ClientToLogicProtocol;

import java.util.HashMap;
import java.util.Map;

public interface ClientMsgHandler<T extends Message> {

    Map<Short, ClientMsgHandler<Message>> handlers = new HashMap<>();

    static void register(ClientMsgHandler<? extends Message> handler) {
        if (handlers.containsKey(handler.getProtocol().getCode())) {
            throw new IllegalArgumentException("Duplicate protocol:" + handler.getProtocol());
        }
        handlers.put(handler.getProtocol().getCode(), (ClientMsgHandler<Message>) handler);
    }

    static ClientMsgHandler<Message> getHandler(short code) {
        return handlers.get(code);
    }

    void handle(Channel channel, int playerId, T message, Config config);

    ClientToLogicProtocol getProtocol();
}
