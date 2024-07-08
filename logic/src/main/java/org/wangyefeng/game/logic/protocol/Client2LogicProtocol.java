package org.wangyefeng.game.logic.protocol;

import com.google.protobuf.Parser;

import java.util.HashMap;
import java.util.Map;

public enum Client2LogicProtocol implements Protocol {

    PING(0),

    ;

    private final int code;

    private final Parser parser;

    private static final Map<Integer, Client2LogicProtocol> PROTOCOLS = new HashMap<>();

    Client2LogicProtocol(int code) {
        this(code, null);
    }

    Client2LogicProtocol(int code, Parser parser) {
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
        for (Client2LogicProtocol protocol : Client2LogicProtocol.values()) {
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
