package org.game.proto.protocol;

import com.google.protobuf.Parser;
import org.game.proto.Topic;
import org.game.proto.struct.Login;
import org.springframework.util.Assert;

public enum GateToClientProtocol implements Protocol {
    PONG((short) 1),

    KICK_OUT((short) 2),

    PLAYER_TOKEN_VALIDATE((short) 3, Login.PbAuthResp.parser()),

    ;

    private final short code;

    private final Parser<?> parser;

    GateToClientProtocol(short code) {
        this(code, null);
    }

    GateToClientProtocol(short code, Parser<?> parser) {
        Assert.isTrue(code > 0, "协议号必须大于0");
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
