package org.wangyefeng.game.client;

public enum C2SProtocol implements Protocol {

    PING((short) 0),

    TOKEN_VALIDATE((short) 1),

    TEST((short) 2),

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
