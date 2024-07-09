package org.wangyefeng.game.gate.protocol;

import java.util.HashMap;
import java.util.Map;

public enum ToClientProtocol implements Protocol {

    PONG((short) 0),

    TOKEN_VALIDATE((short) 1),

    KICK_OUT((short) 2),

    ;

    private static final Map<Short, Protocol> PROTOCOL_MAP = new HashMap<>();

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

    static {
        for (ToClientProtocol value : ToClientProtocol.values()) {
            PROTOCOL_MAP.put(value.code, value);
        }
    }

    public static Protocol getProtocol(int code) {
        return PROTOCOL_MAP.get(code);
    }
}
