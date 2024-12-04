package org.game.logic.handler;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.game.logic.data.mongodb.config.Config;
import org.game.proto.protocol.GateToLogicProtocol;

import java.util.HashMap;
import java.util.Map;

public interface GateMsgHandler<T extends Message> {

    Map<Short, GateMsgHandler<Message>> handlers = new HashMap<>();

    static void register(GateMsgHandler<? extends Message> handler) {
        if (handlers.containsKey(handler.getProtocol().getCode())) {
            throw new IllegalArgumentException("Duplicate protocol:" + handler.getProtocol());
        }
        handlers.put(handler.getProtocol().getCode(), (GateMsgHandler<Message>) handler);
    }

    static GateMsgHandler<Message> getHandler(short code) {
        return handlers.get(code);
    }

    void handle(Channel channel, T msg, Config config);

    GateToLogicProtocol getProtocol();

}
