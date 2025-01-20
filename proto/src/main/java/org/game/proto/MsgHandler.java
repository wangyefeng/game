package org.game.proto;

import com.google.protobuf.Message;
import org.apache.logging.log4j.core.tools.picocli.CommandLine.InitializationException;
import org.game.proto.protocol.Protocol;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public interface MsgHandler {

    Map<Protocol, MsgHandler> handlers = new HashMap<>();

    static void register(MsgHandler handler) {
        MsgHandler oldHandler = handlers.put(handler.getProtocol(), handler);
        if (oldHandler != null) {
            throw new IllegalArgumentException("Duplicate protocol: " + handler.getClass().getSimpleName() + " and " + oldHandler.getClass().getSimpleName());
        }
        if (handler.getProtocol().parser() != null) {
            for (Type type : handler.getClass().getGenericInterfaces()) {
                if (type instanceof ParameterizedType parameterizedType) {
                    Type actualTypeArguments = parameterizedType.getActualTypeArguments()[0];
                    if (actualTypeArguments != handler.getProtocol().parser().getClass()) {
                        throw new IllegalArgumentException("Protocol " + handler.getProtocol() + " and parser " + handler.getProtocol().parser() + " do not match");
                    }
                }
            }
            Type genericSuperclass = handler.getClass().getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType parameterizedType) {
                Type actualTypeArguments = parameterizedType.getActualTypeArguments()[0];
                if (actualTypeArguments instanceof Class clazz && Message.class.isAssignableFrom(clazz)) {
                    try {
                        // 获取 myStaticMethod 方法对象
                        Method method = clazz.getMethod("parser");
                        // 使用反射调用静态方法（对于静态方法，第一个参数可以为 null）
                        Object invoke = method.invoke(null);
                        if (invoke!= handler.getProtocol().parser()) {
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

    static MsgHandler getHandler(Protocol protocol) {
        return handlers.get(protocol);
    }

    Protocol getProtocol();
}
