package org.game.proto.protocol;

import com.google.protobuf.Parser;
import org.game.proto.Topic;
import org.game.proto.struct.Common;
import org.game.proto.struct.Login;

public enum ClientToGateProtocol implements Protocol {

    // 心跳
    PING((short) 0),

    // 账号验证
    VALIDATE((short) 1, Login.PbValidate.parser()),
    ;


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
