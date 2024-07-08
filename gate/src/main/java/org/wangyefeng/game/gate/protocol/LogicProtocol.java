package org.wangyefeng.game.gate.protocol;

import com.google.protobuf.Parser;

import java.util.HashMap;
import java.util.Map;

public enum LogicProtocol implements Protocol {

    PONG(0),

    ;

    private final int code;

    private final Parser parser;

    private static final Map<Integer, LogicProtocol> PROTOCOLS = new HashMap<>();

    LogicProtocol(int code) {
        this(code, null);
    }

    LogicProtocol(int code, Parser parser) {
        if (code < 0) {
            throw new IllegalArgumentException("code must be non-negative");
        }
        this.code = code;
        this.parser = parser;
    }

    public int getCode() {
        return code;
    }

    public Parser<?> getParser() {
        return parser;
    }

    static {
        for (LogicProtocol protocol : LogicProtocol.values()) {
            if (PROTOCOLS.containsKey(protocol.getCode())) {
                throw new IllegalStateException("duplicate code: " + protocol.getCode() + " for " + protocol + " and " + PROTOCOLS.get(protocol.getCode()));
            }
            PROTOCOLS.put(protocol.getCode(), protocol);
        }
    }

    public static boolean match(int code) {
        return PROTOCOLS.containsKey(code);
    }

    public static Parser getParser(int code) {
        return PROTOCOLS.get(code).getParser();
    }

}
