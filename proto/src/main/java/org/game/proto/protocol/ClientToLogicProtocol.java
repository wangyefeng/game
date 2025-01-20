package org.game.proto.protocol;

import com.google.protobuf.Parser;
import org.game.proto.Topic;
import org.game.proto.struct.Common.PbInt;
import org.game.proto.struct.Login;
import org.springframework.util.Assert;

public enum ClientToLogicProtocol implements Protocol {

    LOGIN((short) 1, PbInt.parser()),

    REGISTER((short) 3, Login.PbRegisterReq.parser()),

    LEVEL_UP((short)4),

    ;
    private final short code;

    private final Parser<?> parser;

    ClientToLogicProtocol(short code) {
        this(code, null);
    }

    ClientToLogicProtocol(short code, Parser<?> parser) {
        Assert.isTrue(code >= 0, "协议号必须大于0");
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
