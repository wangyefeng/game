package org.wangyefeng.game.gate.handler.logic;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.wangyefeng.game.gate.protocol.LogicProtocol;

import java.util.HashMap;
import java.util.Map;

public interface LogicMsgHandler<T extends Message> {

    Map<Short, LogicMsgHandler<Message>> handlers = new HashMap<>();

    static void register(LogicMsgHandler<? extends Message> logicMsgHandler) {
        if (handlers.containsKey(logicMsgHandler.getProtocol().getCode())) {
            throw new IllegalArgumentException("Duplicate protocol:" + logicMsgHandler.getProtocol());
        }
        handlers.put(logicMsgHandler.getProtocol().getCode(), (LogicMsgHandler<Message>) logicMsgHandler);
    }

    static LogicMsgHandler<Message> getHandler(short code) {
        return handlers.get(code);
    }

    void handle(Channel channel, T message);

    LogicProtocol getProtocol();

}
