package org.wangyefeng.game.proto.protocol;

import com.google.protobuf.Parser;
import org.wangyefeng.game.proto.Topic;
import org.wangyefeng.game.proto.struct.Common;

public enum ClientToGateProtocol implements Protocol {

    PING((short) 0),

    TOKEN_VALIDATE((short) 1, Common.PbInt.parser()),

    TEST((short) 2);


    private final short code;

    private final Parser<?> parser;

    ClientToGateProtocol(short code) {
        this(code, null);
    }

    ClientToGateProtocol(short code, Parser<?> parser) {
        this.code = code;
        this.parser = parser;
    }

    @Override
    public Topic from() {
        return Topic.CLIENT;
    }

    @Override
    public Topic to() {
        return Topic.GATE;
    }

    @Override
    public short getCode() {
        return code;
    }

    @Override
    public Parser<?> parser() {
        return parser;
    }
}
