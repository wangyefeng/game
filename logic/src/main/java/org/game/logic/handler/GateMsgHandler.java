package org.game.logic.handler;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.game.config.Configs;
import org.game.proto.protocol.GateToLogicProtocol;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface GateMsgHandler<T extends Message> {

    Map<Short, GateMsgHandler<Message>> handlers = new HashMap<>();

    static void register(GateMsgHandler<? extends Message> handler) {
        if (handlers.containsKey(handler.getProtocol().getCode())) {
            throw new IllegalArgumentException("重复注册协议:" + handler.getProtocol());
        }
        handlers.put(handler.getProtocol().getCode(), (GateMsgHandler<Message>) handler);
    }

    static Optional<GateMsgHandler<Message>> getHandler(short code) {
        return Optional.ofNullable(handlers.get(code));
    }

    void handle(Channel channel, T msg, Configs config);

    GateToLogicProtocol getProtocol();

}
