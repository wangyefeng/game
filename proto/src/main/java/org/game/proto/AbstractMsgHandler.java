package org.game.proto;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;

import java.io.InputStream;
import java.lang.reflect.Method;

public abstract class AbstractMsgHandler<T extends Message> implements MsgHandler<T> {

    private final Parser<T> parser;

    protected AbstractMsgHandler() {
        Class<?> parserClazz = MsgHandlerResolver.resolveGenericType(getClass(), MsgHandler.class);
        try {
            Method method = parserClazz.getMethod("parser");
            parser = (Parser<T>) method.invoke(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to call parser method of handler:" + getClass().getSimpleName(), e);
        }
    }

    @Override
    public T parseFrom(InputStream input) throws InvalidProtocolBufferException {
        return parser.parseFrom(input);
    }
}
