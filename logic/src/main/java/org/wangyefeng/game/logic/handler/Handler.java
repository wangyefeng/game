package org.wangyefeng.game.logic.handler;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.wangyefeng.game.logic.protocol.GateProtocol;

import java.util.HashMap;
import java.util.Map;

public interface Handler<T extends Message> {

    Map<Integer, Handler<Message>> handlers = new HashMap<>();

    static void register(Handler<? extends Message> handler) {
        if (handlers.containsKey(handler.getProtocol().getCode())) {
            throw new IllegalArgumentException("Duplicate protocol:" + handler.getProtocol());
        }
        handlers.put(handler.getProtocol().getCode(), (Handler<Message>) handler);
    }

    static Handler<Message> getHandler(int code) {
        return handlers.get(code);
    }

    void handle(Channel channel, T message);

    GateProtocol getProtocol();

}
