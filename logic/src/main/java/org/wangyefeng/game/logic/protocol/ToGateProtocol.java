package org.wangyefeng.game.logic.protocol;

import org.wangyefeng.game.proto.OutProtocol;

public enum ToGateProtocol implements OutProtocol {

    PONG((short) 0),
    ;

    private final short code;

    ToGateProtocol(short code) {
        if (code < 0) {
            throw new IllegalArgumentException("code must be non-negative");
        }
        this.code = code;
    }

    public short getCode() {
        return code;
    }
}
