package org.game.proto;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.game.proto.protocol.Protocol;

public class MessagePlayer<T extends Message> {

    private int playerId;

    private Protocol protocol;

    private T data;

    public MessagePlayer(int playerId, Protocol protocol, T message) {
        this.playerId = playerId;
        this.protocol = protocol;
        this.data = message;
    }

    public MessagePlayer(int playerId, Protocol protocol) {
        this(playerId, protocol, null);
    }

    public short getCode() {
        return protocol.getCode();
    }

    public T getData() {
        return data;
    }

    public int getPlayerId() {
        return playerId;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("playerId=")
                .append(playerId)
                .append(", protocol=")
                .append(protocol.getClass().getSimpleName())
                .append(".")
                .append(protocol);

        if (data != null) {
            try {
                sb.append(", data=").append(JsonFormat.printer().print(data));
            } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException("Failed to serialize data", e);
            }
        }

        return sb.toString();
    }
}
