package org.wangyefeng.game.logic.net;

import com.google.protobuf.Message;
import org.wangyefeng.game.logic.protocol.Protocol;

public class Client2ServerMessage<T extends Message> {

    private int playerId;

    private int code;

    private T message;

    public Client2ServerMessage(int playerId, int code, T message) {
        this.playerId = playerId;
        this.code = code;
        this.message = message;
    }

    public Client2ServerMessage(int playerId, Protocol protocol, T message) {
        this(playerId, protocol.getCode(), message);
    }

    public Client2ServerMessage(int playerId, Protocol protocol) {
        this(playerId, protocol.getCode(), null);
    }

    public Client2ServerMessage(int playerId, int code) {
        this(playerId, code, null);
    }

    public int getCode() {
        return code;
    }

    public T getMessage() {
        return message;
    }

    public int getPlayerId() {
        return playerId;
    }
}
