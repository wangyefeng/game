package org.game.logic.net;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.game.config.Configs;
import org.game.proto.protocol.ClientToLogicProtocol;
import org.game.proto.protocol.Protocol;

import java.util.HashMap;
import java.util.Map;

public interface ClientMsgHandler<T extends Message> {

    Map<ClientToLogicProtocol, ClientMsgHandler<Message>> handlers = new HashMap<>();

    static void register(ClientMsgHandler<? extends Message> handler) {
        handlers.put(handler.getProtocol(), (ClientMsgHandler<Message>) handler);
    }

    static ClientMsgHandler<Message> getHandler(Protocol protocol) {
        return handlers.get(protocol);
    }

    void handle(Channel channel, int playerId, T data, Configs config);

    ClientToLogicProtocol getProtocol();
}
