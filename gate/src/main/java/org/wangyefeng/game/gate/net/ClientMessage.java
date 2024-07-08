package org.wangyefeng.game.gate.net;

import com.google.protobuf.Message;
import org.wangyefeng.game.gate.protocol.Protocol;

public class ClientMessage<T extends Message> {

    private short code;

    private T message;

    public ClientMessage(Protocol protocol) {
        this(protocol.getCode());
    }

    public ClientMessage(Protocol protocol, T message) {
        this(protocol.getCode(), message);
    }

    public ClientMessage(short code) {
        this(code, null);
    }

    public ClientMessage(short code, T message) {
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
