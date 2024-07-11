package org.wangyefeng.game.gate.protocol;

import com.google.protobuf.Parser;
import org.wangyefeng.game.proto.InProtocol;

import java.util.HashMap;
import java.util.Map;

public enum LogicProtocol implements InProtocol {

    PONG((short) 0),

    ;

    private final short code;

    private final Parser<?> parser;

    private static final Map<Short, LogicProtocol> PROTOCOLS = new HashMap<>();

    LogicProtocol(short code) {
        this(code, null);
    }

    LogicProtocol(short code, Parser<?> parser) {
        if (code < 0) {
            throw new IllegalArgumentException("code must be non-negative");
        }
        this.code = code;
        this.parser = parser;
    }

    public short getCode() {
        return code;
    }

    public Parser<?> parser() {
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

    public static boolean match(short code) {
        return PROTOCOLS.containsKey(code);
    }

    public static Parser getParser(short code) {
        return PROTOCOLS.get(code).parser();
    }

}
