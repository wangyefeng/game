package org.wangyefeng.game.gate.protocol;

import java.util.HashMap;
import java.util.Map;

public enum ToClientProtocol implements Protocol {

    PONG(0),

    ;

    private static final Map<Integer, Protocol> PROTOCOL_MAP = new HashMap<>();

    private final int code;

    ToClientProtocol(int code) {
        if (code < 0) {
            throw new IllegalArgumentException("code must be non-negative");
        }
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    static {
        for (ToClientProtocol value : ToClientProtocol.values()) {
            PROTOCOL_MAP.put(value.code, value);
        }
    }

    public static Protocol getProtocol(int code) {
        return PROTOCOL_MAP.get(code);
    }
}
