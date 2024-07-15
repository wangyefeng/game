package org.wangyefeng.game.proto.protocol;

import com.google.protobuf.Parser;
import org.wangyefeng.game.proto.Topic;

public enum GateToLogicProtocol implements Protocol {
    PING((short) 0),

    ;


    private final short code;

    private final Parser<?> parser;

    GateToLogicProtocol(short code) {
        this(code, null);
    }

    GateToLogicProtocol(short code, Parser<?> parser) {
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