package org.wangyefeng.game.logic.net;

import com.google.protobuf.Message;
import org.wangyefeng.game.logic.protocol.Protocol;

public class GateMessage<T extends Message> {

    private int code;

    private T message;

    public GateMessage(int code, T message) {
        this.code = code;
        this.message = message;
    }

    public GateMessage(Protocol protocol, T message) {
        this(protocol.getCode(), message);
    }

    public GateMessage(Protocol protocol) {
        this(protocol.getCode(), null);
    }

    public GateMessage(int code) {
        this(code, null);
    }

    public int getCode() {
        return code;
    }

    public T getMessage() {
        return message;
    }
}
