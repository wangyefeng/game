package org.game.proto.protocol;

import org.game.proto.Topic;
import org.springframework.util.Assert;

public enum ClientToGateProtocol implements Protocol {

    // 心跳
    PING((short) 1),

    // 玩家验证
    AUTH((short) 2),

    // 获取服务器时间
    GET_SERVER_TIME((short) 3),
    ;

    private final short code;

    ClientToGateProtocol(short code) {
        Assert.isTrue(code > 0, "协议号必须大于0");
        this.code = code;
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

}
