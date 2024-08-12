package org.game.proto;

import com.google.protobuf.Message;
import org.game.proto.protocol.Protocol;

public class MessagePlayer<T extends Message> {

    private int playerId;

    private Protocol protocol;

    private T message;

    public MessagePlayer(int playerId, Protocol protocol, T message) {
        this.playerId = playerId;
        this.protocol = protocol;
        this.message = message;
    }

    public MessagePlayer(int playerId, Protocol protocol) {
        this(playerId, protocol, null);
    }

    public short getCode() {
        return protocol.getCode();
    }

    public T getMessage() {
        return message;
    }

    public int getPlayerId() {
        return playerId;
    }

    public Protocol getProtocol() {
        return protocol;
    }
}
