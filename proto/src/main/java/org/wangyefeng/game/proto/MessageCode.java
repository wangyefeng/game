package org.wangyefeng.game.proto;

import com.google.protobuf.Message;
import org.wangyefeng.game.proto.protocol.Protocol;

public class MessageCode<T extends Message> {

    private Protocol protocol;

    private T message;


    public MessageCode(Protocol protocol, T message) {
        this.protocol = protocol;
        this.message = message;
    }

    public MessageCode(Protocol protocol) {
        this(protocol, null);
    }

    public T getMessage() {
        return message;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public short getCode() {
        return protocol.getCode();
    }
}
