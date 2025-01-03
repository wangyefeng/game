package org.game.proto.protocol;

import com.google.protobuf.Parser;
import org.game.proto.Topic;
import org.game.proto.struct.Login;
import org.springframework.util.Assert;

public enum ClientToGateProtocol implements Protocol {

    // 心跳
    PING((short) 0),

    // 账号验证
    ACCOUNT_VALIDATE((short) 1, Login.PbAccountValidateReq.parser()),

    // 玩家验证
    PLAYER_VALIDATE((short) 2, Login.PbPlayerValidateReq.parser()),
    ;


    private final short code;

    private final Parser<?> parser;

    ClientToGateProtocol(short code) {
        this(code, null);
    }

    ClientToGateProtocol(short code, Parser<?> parser) {
        Assert.isTrue(code >= 0, "协议号必须大于0");
        this.code = code;
        this.parser = parser;
    }

    static {
        Protocols.addProtocols(ClientToGateProtocol.values());
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
