package org.wyf.game.logic;

public enum ExitStatus {

    NORMAL(0),

    ZOOKEEPER_CONNECTION_LOST(1),
    ;

    private final int code;

    ExitStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
