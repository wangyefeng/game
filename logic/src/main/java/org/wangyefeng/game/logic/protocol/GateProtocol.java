package org.wangyefeng.game.logic.protocol;

import com.google.protobuf.Parser;
import org.wangyefeng.game.proto.InProtocol;
import org.wangyefeng.game.proto.struct.Common;

/**
 * @author wangyefeng
 * @date 2024-07-08
 * @description 服务器到客户端协议
 */
public enum GateProtocol implements InProtocol {

    PING((short) 0),
    LOGIN((short) 1, Common.PbInt.parser());

    private final short code;

    private final Parser parser;

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

    public Parser<?> parser() {
        return parser;
    }
}
