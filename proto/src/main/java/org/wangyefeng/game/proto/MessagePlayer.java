package org.wangyefeng.game.proto;

import com.google.protobuf.Message;

public class MessagePlayer<T extends Message> {

    private int playerId;

    private short code;

    private T message;

    public MessagePlayer(int playerId, short code, T message) {
        this.playerId = playerId;
        this.code = code;
        this.message = message;
    }

    public MessagePlayer(int playerId, Protocol protocol, T message) {
        this(playerId, protocol.getCode(), message);
    }

    public MessagePlayer(int playerId, Protocol protocol) {
        this(playerId, protocol.getCode(), null);
    }

    public MessagePlayer(int playerId, short code) {
        this(playerId, code, null);
    }

    public short getCode() {
        return code;
    }

    public T getMessage() {
        return message;
    }

    public int getPlayerId() {
        return playerId;
    }
}
