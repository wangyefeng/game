package org.game.proto;

import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import org.game.proto.protocol.Protocol;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public interface MsgHandler<T extends Message> {

    Map<Protocol, HandlerAndParser<? extends Message>> handlers = new HashMap<>();

    static <T extends Message> void register(MsgHandler<T> handler) {
        if (handlers.containsKey(handler.getProtocol())) {
            throw new IllegalArgumentException("Duplicate protocol: " + handler.getClass().getSimpleName() + " and " + handlers.get(handler.getProtocol()).handler.getClass().getSimpleName());
        }
        Class<?> parserClazz = MsgHandlerResolver.resolveGenericType(handler.getClass(), MsgHandler.class);
        try {
            Method method = parserClazz.getMethod("parser");
            @SuppressWarnings("unchecked")
            Parser<T> parser = (Parser<T>) method.invoke(null);
            handlers.put(handler.getProtocol(), HandlerAndParser.of(handler, parser));
        } catch (Exception e) {
            throw new RuntimeException("Failed to call parser method of handler:" + handler.getClass().getSimpleName(), e);
        }
    }

    static MsgHandler<? extends Message> getHandler(Protocol protocol) {
        return handlers.get(protocol).handler;
    }

    static Parser<? extends Message> getParser(Protocol protocol) {
        return handlers.get(protocol).parser;
    }

    Protocol getProtocol();

    record HandlerAndParser<M extends Message>(MsgHandler<M> handler, Parser<M> parser) {

        public static <M extends Message> HandlerAndParser<M> of(MsgHandler<M> handler, Parser<M> parser) {
            return new HandlerAndParser<>(handler, parser);
        }
    }
}
