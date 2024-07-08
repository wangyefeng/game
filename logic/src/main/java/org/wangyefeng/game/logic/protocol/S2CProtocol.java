package org.wangyefeng.game.logic.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangyefeng
 * @date 2024-07-08
 * @description 服务器到客户端协议
 */
public enum S2CProtocol implements Protocol {

    PONG(0),

    ;

    private static final Map<Integer, S2CProtocol> PROTOCOL_MAP = new HashMap<>();

    private final int code;

    S2CProtocol(int code) {
        if (code < 0) {
            throw new IllegalArgumentException("code must be non-negative");
        }
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    static {
        for (S2CProtocol value : S2CProtocol.values()) {
            PROTOCOL_MAP.put(value.code, value);
        }
    }

    public static S2CProtocol getProtocol(int code) {
        return PROTOCOL_MAP.get(code);
    }
}
