package org.game.proto.protocol;

import com.google.protobuf.Parser;
import org.game.proto.Topic;
import org.game.proto.struct.Login;

public enum LogicToClientProtocol implements Protocol {
    LOGIN((short) 1, Login.PbLoginResp.parser()),

    REGISTER((short) 3, Login.PbLoginResp.parser()),

    ;


    private final short code;

    private final Parser<?> parser;

    LogicToClientProtocol(short code) {
        this(code, null);
    }

    LogicToClientProtocol(short code, Parser<?> parser) {
        this.code = code;
        this.parser = parser;
    }

    @Override
    public Topic from() {
        return Topic.LOGIC;
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
