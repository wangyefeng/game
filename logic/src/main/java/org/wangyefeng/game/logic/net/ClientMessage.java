package org.wangyefeng.game.logic.net;

import com.google.protobuf.Message;
import org.wangyefeng.game.logic.protocol.Protocol;

public class ClientMessage<T extends Message> {

    private int playerId;

    private short code;

    private T message;

    public ClientMessage(int playerId, short code, T message) {
        this.playerId = playerId;
        this.code = code;
        this.message = message;
    }

    public ClientMessage(int playerId, Protocol protocol, T message) {
        this(playerId, protocol.getCode(), message);
    }

    public ClientMessage(int playerId, Protocol protocol) {
        this(playerId, protocol.getCode(), null);
    }

    public ClientMessage(int playerId, short code) {
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
