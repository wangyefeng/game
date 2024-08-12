package org.game.gate.handler.client;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.game.proto.protocol.ClientToGateProtocol;

import java.util.HashMap;
import java.util.Map;

public interface ClientMsgHandler<T extends Message> {

    Map<Short, ClientMsgHandler<Message>> handlers = new HashMap<>();

    static void register(ClientMsgHandler<? extends Message> logicHandler) {
        if (handlers.containsKey(logicHandler.getProtocol().getCode())) {
            throw new IllegalArgumentException("Duplicate protocol:" + logicHandler.getProtocol());
        }
        handlers.put(logicHandler.getProtocol().getCode(), (ClientMsgHandler<Message>) logicHandler);
    }

    static ClientMsgHandler<Message> getHandler(short code) {
        return handlers.get(code);
    }

    void handle(Channel channel, T msg) throws Exception;

    ClientToGateProtocol getProtocol();

}
