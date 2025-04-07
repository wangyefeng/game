package org.game.proto;

import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import org.game.proto.protocol.Protocol;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public interface MsgHandler<T extends Message> {

    Map<Protocol, HandlerAndParser> handlers = new HashMap<>();

    static void register(MsgHandler handler) {
        if (handlers.containsKey(handler.getProtocol())) {
            throw new IllegalArgumentException("Duplicate protocol: " + handler.getClass().getSimpleName() + " and " + handlers.get(handler.getProtocol()).handler.getClass().getSimpleName());
        }
        Class<?> parserClazz = MsgHandlerResolver.resolveGenericType(handler.getClass(), MsgHandler.class);
        try {
            Method method = parserClazz.getMethod("parser");
            Parser parser = (Parser) method.invoke(null);
            handlers.put(handler.getProtocol(), new HandlerAndParser(handler, parser));
        } catch (Exception e) {
            throw new RuntimeException("Failed to call parser method of handler:" + handler.getClass().getSimpleName(), e);
        }
    }

    static MsgHandler getHandler(Protocol protocol) {
        return handlers.get(protocol).handler;
    }

    static Parser<?> getParser(Protocol protocol) {
        return handlers.get(protocol).parser;
    }

    Protocol getProtocol();

    record HandlerAndParser<M extends Message>(MsgHandler<M> handler, Parser<M> parser) {}
}
