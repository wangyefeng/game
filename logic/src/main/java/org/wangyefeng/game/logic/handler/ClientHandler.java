package org.wangyefeng.game.logic.handler;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.wangyefeng.game.logic.protocol.ClientProtocol;

import java.util.HashMap;
import java.util.Map;

public interface ClientHandler<T extends Message> {

    Map<Short, ClientHandler<Message>> handlers = new HashMap<>();

    static void register(ClientHandler<? extends Message> handler) {
        if (handlers.containsKey(handler.getProtocol().getCode())) {
            throw new IllegalArgumentException("Duplicate protocol:" + handler.getProtocol());
        }
        handlers.put(handler.getProtocol().getCode(), (ClientHandler<Message>) handler);
    }

    static ClientHandler<Message> getHandler(int code) {
        return handlers.get(code);
    }

    void handle(Channel channel, int playerId, T message);

    ClientProtocol getProtocol();
}
