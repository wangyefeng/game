package org.wangyefeng.game.logic.protocol;

import org.wangyefeng.game.proto.OutProtocol;

public enum ToClientProtocol implements OutProtocol {

    LOGIN((short) 3),
    ;

    private final short code;

    ToClientProtocol(short code) {
        if (code < 0) {
            throw new IllegalArgumentException("code must be non-negative");
        }
        this.code = code;
    }

    public short getCode() {
        return code;
    }
}
