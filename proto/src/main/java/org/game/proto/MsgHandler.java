package org.game.proto;

import org.game.proto.protocol.Protocol;

import java.util.HashMap;
import java.util.Map;

public interface MsgHandler {

    Map<Protocol, MsgHandler> handlers = new HashMap<>();

    static void register(MsgHandler handler) {
        MsgHandler oldHandler = handlers.put(handler.getProtocol(), handler);
        if (oldHandler != null) {
            throw new IllegalArgumentException("Duplicate protocol: " + handler.getClass().getSimpleName() + " and " + oldHandler.getClass().getSimpleName());
        }
    }

    static MsgHandler getHandler(Protocol protocol) {
        return handlers.get(protocol);
    }

    Protocol getProtocol();
}
