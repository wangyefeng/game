package org.wangyefeng.game.logic.net;

import com.google.protobuf.Message;
import org.wangyefeng.game.logic.protocol.Protocol;

public class GateMessage<T extends Message> {

    private short code;

    private T message;

    public GateMessage(short code, T message) {
        this.code = code;
        this.message = message;
    }

    public GateMessage(Protocol protocol, T message) {
        this(protocol.getCode(), message);
    }

    public GateMessage(Protocol protocol) {
        this(protocol.getCode(), null);
    }

    public GateMessage(short code) {
        this(code, null);
    }

    public short getCode() {
        return code;
    }

    public T getMessage() {
        return message;
    }
}
