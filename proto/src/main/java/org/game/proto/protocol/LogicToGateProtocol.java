package org.game.proto.protocol;

import com.google.protobuf.Parser;
import org.game.proto.Topic;

public enum LogicToGateProtocol implements Protocol {
    PING((short) 1),

    KICK_OUT((short) 2),
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
