package org.wyf.game.proto;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;

import java.io.InputStream;
import java.lang.reflect.Method;

public abstract class AbstractMsgHandler<M extends Message> implements MsgHandler<M> {

    private final Parser<M> parser;

    protected AbstractMsgHandler() {
        Class<M> parserClazz = (Class<M>) MsgHandlerResolver.resolveGenericType(getClass(), MsgHandler.class);
        try {
            Method method = parserClazz.getMethod("parser");
            parser = (Parser<M>) method.invoke(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to call parser method of handler:" + getClass().getSimpleName(), e);
        }
    }

    @Override
    public M parseFrom(InputStream input) throws InvalidProtocolBufferException {
        return parser.parseFrom(input);
    }
}
