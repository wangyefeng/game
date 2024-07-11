package org.wangyefeng.game.client;

import org.wangyefeng.game.proto.Protocol;

public enum C2SProtocol implements Protocol {

    PING((short) 0),

    TOKEN_VALIDATE((short) 1),

    TEST((short) 2),

    LOGIN((short) 3),

    ;

    private final short code;

    C2SProtocol(short code) {
        if (code < 0) {
            throw new IllegalArgumentException("code must be non-negative");
        }
        this.code = code;
    }

    public short getCode() {
        return code;
    }
}
