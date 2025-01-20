package org.game.proto;

import com.google.protobuf.Message;
import org.apache.logging.log4j.core.tools.picocli.CommandLine.InitializationException;
import org.game.proto.protocol.Protocol;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface MsgHandler {

    Map<Protocol, MsgHandler> handlers = new HashMap<>();

    static void register(MsgHandler handler) {
        MsgHandler oldHandler = handlers.put(handler.getProtocol(), handler);
        if (oldHandler != null) {
            throw new IllegalArgumentException("Duplicate protocol: " + handler.getClass().getSimpleName() + " and " + oldHandler.getClass().getSimpleName());
        }
        if (handler.getProtocol().parser() != null) {
            List<Type> types = new ArrayList<>(List.of(handler.getClass().getGenericInterfaces()));
            types.add(handler.getClass().getGenericSuperclass());
            for (Type type : types) {
                if (type instanceof ParameterizedType parameterizedType) {
                    Type actualTypeArguments = parameterizedType.getActualTypeArguments()[0];
                    if (actualTypeArguments instanceof Class clazz && Message.class.isAssignableFrom(clazz)) {
                        try {
                            Method method = clazz.getMethod("parser");
                            Object invoke = method.invoke(null);
                            if (invoke != handler.getProtocol().parser()) {
                                String aClass = handler.getProtocol().parser().getClass().toString();
                                throw new InitializationException("处理器 " + handler.getClass().getSimpleName() + " 的消息类型和协议的解析器类型不匹配！正确的消息类型是：" + aClass.substring(aClass.indexOf('$') + 1, aClass.lastIndexOf('$')));
                            }
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to call parser method of " + actualTypeArguments, e);
                        }
                    }
                }
            }
        }
    }

    static MsgHandler getHandler(Protocol protocol) {
        return handlers.get(protocol);
    }

    Protocol getProtocol();
}
