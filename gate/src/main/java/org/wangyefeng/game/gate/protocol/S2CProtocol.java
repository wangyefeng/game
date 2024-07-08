package org.wangyefeng.game.gate.protocol;

import org.wangyefeng.game.common.Server;

import java.util.HashMap;
import java.util.Map;

public enum S2CProtocol implements Protocol {

    PONG(0, Server.GATE),

    ;

    private static final Map<Integer, Protocol> PROTOCOL_MAP = new HashMap<>();

    private final int code;

    private final byte server;

    S2CProtocol(int code, Server server) {
        if (code < 0) {
            throw new IllegalArgumentException("code must be non-negative");
        }
        this.code = code;
        this.server = server.getCode();
    }

    public int getCode() {
        return code;
    }

    public byte getServer() {
        return server;
    }

    static {
        for (S2CProtocol value : S2CProtocol.values()) {
            PROTOCOL_MAP.put(value.code, value);
        }
    }

    public static Protocol getProtocol(int code) {
        return PROTOCOL_MAP.get(code);
    }
}
