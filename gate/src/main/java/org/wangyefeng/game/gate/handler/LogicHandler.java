package org.wangyefeng.game.gate.handler;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.wangyefeng.game.gate.protocol.LogicProtocol;

import java.util.HashMap;
import java.util.Map;

public interface LogicHandler<T extends Message> {

    Map<Integer, LogicHandler<Message>> handlers = new HashMap<>();

    static void register(LogicHandler<? extends Message> logicHandler) {
        if (handlers.containsKey(logicHandler.getProtocol().getCode())) {
            throw new IllegalArgumentException("Duplicate protocol:" + logicHandler.getProtocol());
        }
        handlers.put(logicHandler.getProtocol().getCode(), (LogicHandler<Message>) logicHandler);
    }

    static LogicHandler<Message> getHandler(int code) {
        return handlers.get(code);
    }

    void handle(Channel channel, T message);

    LogicProtocol getProtocol();

}
