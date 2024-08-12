package org.game.proto.protocol;

import com.google.protobuf.Parser;
import org.game.proto.Topic;

public enum GateToClientProtocol implements Protocol {
    PONG((short) 0),

    TOKEN_VALIDATE((short) 1),

    KICK_OUT((short) 2);


    private final short code;

    private final Parser<?> parser;

    GateToClientProtocol(short code) {
        this(code, null);
    }

    GateToClientProtocol(short code, Parser<?> parser) {
        this.code = code;
        this.parser = parser;
    }

    @Override
    public Topic from() {
        return Topic.GATE;
    }

    @Override
    public Topic to() {
        return Topic.CLIENT;
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
