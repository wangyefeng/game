package org.game.proto.protocol;

import com.google.protobuf.Parser;
import org.game.proto.Topic;
import org.game.proto.struct.Common;
import org.game.proto.struct.Login;

public enum ClientToLogicProtocol implements Protocol {

    LOGIN((short) 1, Login.PbLogin.parser()),

    TEST((short) 2, Common.PbInt.parser()),

    REGISTER((short) 3, Login.PbRegister.parser()),

    ;
    private final short code;

    private final Parser<?> parser;

    ClientToLogicProtocol(short code) {
        this(code, null);
    }

    ClientToLogicProtocol(short code, Parser<?> parser) {
        this.code = code;
        this.parser = parser;
    }

    @Override
    public Topic from() {
        return Topic.CLIENT;
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
