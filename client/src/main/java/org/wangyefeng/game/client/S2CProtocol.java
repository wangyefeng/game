package org.wangyefeng.game.client;

import com.google.protobuf.Parser;
import org.wangyefeng.game.proto.Protocol;

import java.util.HashMap;
import java.util.Map;

public enum S2CProtocol implements Protocol {

    PING((short) 0),

    TOKEN_VALIDATE((short) 1),

    TEST((short) 2),

    LOGIN((short) 3),

    ;

    private final short code;

    private final Parser parser;

    private static final Map<Short, S2CProtocol> PROTOCOLS = new HashMap<>();

    S2CProtocol(short code) {
        this(code, null);
    }

    S2CProtocol(short code, Parser parser) {
        if (code < 0) {
            throw new IllegalArgumentException("code must be non-negative");
        }
        this.code = code;
        this.parser = parser;
    }

    public short getCode() {
        return code;
    }

    public Parser<?> getParser() {
        return parser;
    }

    static {
        for (S2CProtocol protocol : S2CProtocol.values()) {
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
        return PROTOCOLS.get(code).getParser();
    }
}
