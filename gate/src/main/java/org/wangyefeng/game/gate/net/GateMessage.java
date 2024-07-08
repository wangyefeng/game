package org.wangyefeng.game.gate.net;

import com.google.protobuf.Message;
import org.wangyefeng.game.gate.protocol.Protocol;

public class GateMessage<T extends Message> {

    private int code;

    private T message;

    public GateMessage(Protocol protocol) {
        this(protocol.getCode());
    }

    public GateMessage(Protocol protocol, T message) {
        this(protocol.getCode(), message);
    }

    public GateMessage(int code) {
        this(code, null);
    }

    public GateMessage(int code, T message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public T getMessage() {
        return message;
    }
}
