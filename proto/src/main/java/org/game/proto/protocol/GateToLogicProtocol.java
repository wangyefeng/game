package org.game.proto.protocol;

import com.google.protobuf.Parser;
import org.game.proto.struct.Common;
import org.game.proto.Topic;
import org.springframework.util.Assert;

public enum GateToLogicProtocol implements Protocol {
    PING((short) 0),

    LOGOUT((short) 1, Common.PbInt.parser()),
    ;


    private final short code;

    private final Parser<?> parser;

    GateToLogicProtocol(short code) {
        this(code, null);
    }

    GateToLogicProtocol(short code, Parser<?> parser) {
        Assert.isTrue(code >= 0, "协议号必须大于0");
        this.code = code;
        this.parser = parser;
    }

    @Override
    public Topic from() {
        return Topic.GATE;
    }

    @Override
    public Topic to() {
        return Topic.LOGIC;
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
