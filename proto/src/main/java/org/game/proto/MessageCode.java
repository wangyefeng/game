package org.game.proto;

import com.google.protobuf.Message;
import org.game.proto.protocol.Protocol;
import org.game.proto.util.ProtobufJsonUtil;

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("protocol=")
                .append(protocol.getClass().getSimpleName())
                .append(".")
                .append(protocol);

        if (data != null) {
            sb.append(", data=").append(ProtobufJsonUtil.serializeMessage(data));
        }

        return sb.toString();
    }

}
