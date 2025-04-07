package org.game.proto;

import com.google.protobuf.Message;
import org.game.proto.protocol.Protocol;
import org.game.proto.util.ProtobufJsonUtil;

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
            sb.append(", data=").append(ProtobufJsonUtil.serializeMessage(data));
        }

        return sb.toString();
    }
}
