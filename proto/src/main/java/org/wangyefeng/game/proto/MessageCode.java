package org.wangyefeng.game.proto;

import com.google.protobuf.Message;

public class MessageCode<T extends Message> {

    private short code;

    private T message;

    public MessageCode(short code, T message) {
        this.code = code;
        this.message = message;
    }

    public MessageCode(Protocol protocol, T message) {
        this(protocol.getCode(), message);
    }

    public MessageCode(Protocol protocol) {
        this(protocol.getCode(), null);
    }

    public MessageCode(short code) {
        this(code, null);
    }

    public short getCode() {
        return code;
    }

    public T getMessage() {
        return message;
    }
}
