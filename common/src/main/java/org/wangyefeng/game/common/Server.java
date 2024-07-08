package org.wangyefeng.game.common;

public enum Server {

    GATE((byte) 1),

    LOGIC((byte) 2),

    CROSS((byte) 3),

    CLIENT((byte) 4),
    ;

    private final byte code;

    Server(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }
}
