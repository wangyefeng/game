package org.game.proto;

import com.google.protobuf.Message;
import org.game.proto.protocol.Protocol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息处理器工厂
 */
public class MsgHandlerFactory {

    private final Map<Protocol, MsgHandler<? extends Message>> handlerMap = new HashMap<>();

    // 构造函数中把所有实现注册进 Map
    public MsgHandlerFactory(List<MsgHandler<? extends Message>> handlerList) {
        handlerList.forEach(this::register);
    }

    private <T extends Message> void register(MsgHandler<T> handler) {
        if (handlerMap.containsKey(handler.getProtocol())) {
            throw new IllegalArgumentException("Duplicate protocol: " + handler.getClass().getSimpleName() + " and " + handlerMap.get(handler.getProtocol()).getClass().getSimpleName());
        }
        handlerMap.put(handler.getProtocol(), handler);
    }

    public MsgHandler<? extends Message> getHandler(Protocol protocol) {
        return handlerMap.get(protocol);
    }
}
