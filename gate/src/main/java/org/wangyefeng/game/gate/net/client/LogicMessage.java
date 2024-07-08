package org.wangyefeng.game.gate.net.client;

import com.google.protobuf.Message;
import org.wangyefeng.game.gate.protocol.Protocol;

public class LogicMessage<T extends Message> {

    private int playerId;

    private int code;

    private T message;

    public LogicMessage(int playerId, int code, T message) {
        this.playerId = playerId;
        this.code = code;
        this.message = message;
    }

    public LogicMessage(int playerId, Protocol protocol, T message) {
        this(playerId, protocol.getCode(), message);
    }

    public LogicMessage(int playerId, Protocol protocol) {
        this(playerId, protocol.getCode(), null);
    }

    public LogicMessage(int playerId, int code) {
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
