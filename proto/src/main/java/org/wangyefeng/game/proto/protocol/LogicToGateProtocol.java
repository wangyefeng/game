package org.wangyefeng.game.proto.protocol;

import com.google.protobuf.Parser;
import org.wangyefeng.game.proto.Topic;

public enum LogicToGateProtocol implements Protocol {
    PONG((short) 0),

    ;
    private final short code;

    private final Parser<?> parser;

    LogicToGateProtocol(short code) {
        this(code, null);
    }

    LogicToGateProtocol(short code, Parser<?> parser) {
        this.code = code;
        this.parser = parser;
    }

    @Override
    public Topic from() {
        return Topic.LOGIC;
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
