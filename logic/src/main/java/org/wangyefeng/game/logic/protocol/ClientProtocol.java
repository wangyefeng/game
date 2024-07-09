package org.wangyefeng.game.logic.protocol;

import com.google.protobuf.Parser;
import org.wangyefeng.game.proto.Common;

import java.util.HashMap;
import java.util.Map;

public enum ClientProtocol implements Protocol {

    LOGIN((short) 3, Common.PbInt.parser());

    private final short code;

    private final Parser parser;

    private static final Map<Short, ClientProtocol> PROTOCOLS = new HashMap<>();

    ClientProtocol(short code) {
        this(code, null);
    }

    ClientProtocol(short code, Parser parser) {
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
        for (ClientProtocol protocol : ClientProtocol.values()) {
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
