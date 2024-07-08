package org.wangyefeng.game.logic.handler;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.wangyefeng.game.logic.protocol.GateProtocol;

import java.util.HashMap;
import java.util.Map;

public interface GateHandler<T extends Message> {

    Map<Integer, GateHandler<Message>> handlers = new HashMap<>();

    static void register(GateHandler<? extends Message> handler) {
        if (handlers.containsKey(handler.getProtocol().getCode())) {
            throw new IllegalArgumentException("Duplicate protocol:" + handler.getProtocol());
        }
        handlers.put(handler.getProtocol().getCode(), (GateHandler<Message>) handler);
    }

    static GateHandler<Message> getHandler(int code) {
        return handlers.get(code);
    }

    void handle(Channel channel, T message);

    GateProtocol getProtocol();

}
