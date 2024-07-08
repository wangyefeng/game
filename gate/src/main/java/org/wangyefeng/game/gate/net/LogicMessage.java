package org.wangyefeng.game.gate.net;

import com.google.protobuf.Message;
import org.wangyefeng.game.gate.protocol.Protocol;

public class LogicMessage<T extends Message> {

    private short code;

    private T message;

    public LogicMessage(Protocol protocol) {
        this(protocol.getCode());
    }

    public LogicMessage(Protocol protocol, T message) {
        this(protocol.getCode(), message);
    }

    public LogicMessage(short code) {
        this(code, null);
    }

    public LogicMessage(short code, T message) {
        this.code = code;
        this.message = message;
    }

    public short getCode() {
        return code;
    }

    public T getMessage() {
        return message;
    }
}
