package org.wangyefeng.game.gate.net.client;

import com.google.protobuf.Message;
import org.wangyefeng.game.gate.protocol.Protocol;

public class LogicMessage<T extends Message> {

    private int code;

    private T message;

    public LogicMessage(int code, T message) {
        this.code = code;
        this.message = message;
    }

    public LogicMessage(Protocol protocol, T message) {
        this(protocol.getCode(), message);
    }

    public LogicMessage(Protocol protocol) {
        this(protocol.getCode(), null);
    }

    public LogicMessage(int code) {
        this(code, null);
    }

    public int getCode() {
        return code;
    }

    public T getMessage() {
        return message;
    }
}
