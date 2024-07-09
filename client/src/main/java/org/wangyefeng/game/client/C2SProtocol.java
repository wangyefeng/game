package org.wangyefeng.game.client;

import com.google.protobuf.Parser;
import org.wangyefeng.game.proto.Common;

import java.util.HashMap;
import java.util.Map;

public enum C2SProtocol implements Protocol {

    PING((short) 0),

    LOGIN((short) 1, Common.PbInt.parser()),

    ;

    private final short code;

    private final Parser parser;

    private static final Map<Short, C2SProtocol> PROTOCOLS = new HashMap<>();

    C2SProtocol(short code) {
        this(code, null);
    }

    C2SProtocol(short code, Parser parser) {
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
        for (C2SProtocol protocol : C2SProtocol.values()) {
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
