package org.wangyefeng.game.logic.protocol;

public enum ToGateProtocol {

    PONG(0),
    ;

    private final int code;

    ToGateProtocol(int code) {
        if (code < 0) {
            throw new IllegalArgumentException("code must be non-negative");
        }
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
