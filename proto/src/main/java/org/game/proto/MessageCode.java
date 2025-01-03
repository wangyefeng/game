package org.game.proto;

import com.google.protobuf.Message;
import org.game.proto.protocol.Protocol;

public class MessageCode<T extends Message> {

    private Protocol protocol;

    private T data;


    public MessageCode(Protocol protocol, T data) {
        this.protocol = protocol;
        this.data = data;
    }

    public MessageCode(Protocol protocol) {
        this(protocol, null);
    }

    public T getData() {
        return data;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public short getCode() {
        return protocol.getCode();
    }
}
