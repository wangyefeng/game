package org.wangyefeng.game.logic.net;

import com.google.protobuf.Message;
import org.wangyefeng.game.logic.protocol.Protocol;

public class Gate2ServerMessage<T extends Message> {

    private int code;

    private T message;

    public Gate2ServerMessage(int code, T message) {
        this.code = code;
        this.message = message;
    }

    public Gate2ServerMessage(Protocol protocol, T message) {
        this(protocol.getCode(), message);
    }

    public Gate2ServerMessage(Protocol protocol) {
        this(protocol.getCode(), null);
    }

    public Gate2ServerMessage(int code) {
        this(code, null);
    }

    public int getCode() {
        return code;
    }

    public T getMessage() {
        return message;
    }
}
