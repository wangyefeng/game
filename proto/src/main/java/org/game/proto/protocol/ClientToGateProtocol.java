package org.game.proto.protocol;

import com.google.protobuf.Parser;
import org.game.proto.Topic;
import org.game.proto.struct.Login;
import org.game.proto.struct.System.PbGetServerTimeReq;
import org.springframework.util.Assert;

public enum ClientToGateProtocol implements Protocol {

    // 心跳
    PING((short) 1),

    // 玩家验证
    AUTH((short) 2, Login.PbAuthReq.parser()),

    // 获取服务器时间
    GET_SERVER_TIME((short) 3, PbGetServerTimeReq.parser()),
    ;


    private final short code;

    private final Parser<?> parser;

    ClientToGateProtocol(short code) {
        this(code, null);
    }

    ClientToGateProtocol(short code, Parser<?> parser) {
        Assert.isTrue(code > 0, "协议号必须大于0");
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
