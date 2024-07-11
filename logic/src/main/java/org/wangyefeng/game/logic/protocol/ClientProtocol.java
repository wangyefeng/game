package org.wangyefeng.game.logic.protocol;

import com.google.protobuf.Parser;
import org.wangyefeng.game.proto.InProtocol;
import org.wangyefeng.game.proto.struct.Common;

public enum ClientProtocol implements InProtocol {

    LOGIN((short) 3, Common.PbInt.parser());

    private final short code;

    private final Parser parser;

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

    public Parser<?> parser() {
        return parser;
    }

}
