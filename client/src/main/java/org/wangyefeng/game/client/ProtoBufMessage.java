package org.wangyefeng.game.client;

import com.google.protobuf.Message;

public class ProtoBufMessage<T extends Message> {

    private short code;

    private T message;

    public ProtoBufMessage(Protocol protocol) {
        this(protocol.getCode());
    }

    public ProtoBufMessage(Protocol protocol, T message) {
        this(protocol.getCode(), message);
    }

    public ProtoBufMessage(short code) {
        this(code, null);
    }

    public ProtoBufMessage(short code, T message) {
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
