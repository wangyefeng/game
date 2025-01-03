package org.game.logic.net;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.game.config.Configs;
import org.game.proto.protocol.GateToLogicProtocol;
import org.game.proto.protocol.Protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface GateMsgHandler<T extends Message> {

    Map<GateToLogicProtocol, GateMsgHandler<Message>> handlers = new HashMap<>();

    static void register(GateMsgHandler<? extends Message> handler) {
        handlers.put(handler.getProtocol(), (GateMsgHandler<Message>) handler);
    }

    static Optional<GateMsgHandler<Message>> getHandler(Protocol protocol) {
        return Optional.of(handlers.get(protocol));
    }

    void handle(Channel channel, T msg, Configs config);

    GateToLogicProtocol getProtocol();

}
