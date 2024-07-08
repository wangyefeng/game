package org.wangyefeng.game.logic.protocol;

import com.google.protobuf.Parser;
import org.wangyefeng.game.proto.Common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangyefeng
 * @date 2024-07-08
 * @description 服务器到客户端协议
 */
public enum GateProtocol implements Protocol {

    PING((short) 0),
    LOGIN((short) 1, Common.PbInt.parser());

    private final short code;

    private final Parser parser;

    private static final Map<Short, GateProtocol> PROTOCOLS = new HashMap<>();

    GateProtocol(short code) {
        this(code, null);
    }

    GateProtocol(short code, Parser parser) {
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
        for (GateProtocol protocol : GateProtocol.values()) {
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
