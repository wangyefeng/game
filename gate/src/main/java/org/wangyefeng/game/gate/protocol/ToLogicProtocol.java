package org.wangyefeng.game.gate.protocol;

import org.wangyefeng.game.proto.Protocol;
import org.wangyefeng.game.proto.OutProtocol;

import java.util.HashMap;
import java.util.Map;

public enum ToLogicProtocol implements OutProtocol {

    PING((short) 0),

    ;

    private static final Map<Short, Protocol> PROTOCOL_MAP = new HashMap<>();

    private final short code;

    ToLogicProtocol(short code) {
        if (code < 0) {
            throw new IllegalArgumentException("code must be non-negative");
        }
        this.code = code;
    }

    public short getCode() {
        return code;
    }

    static {
        for (ToLogicProtocol value : ToLogicProtocol.values()) {
            PROTOCOL_MAP.put(value.code, value);
        }
    }

    public static Protocol getProtocol(int code) {
        return PROTOCOL_MAP.get(code);
    }
}
