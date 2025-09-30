package org.wyf.game.proto.protocol;

import org.wyf.game.proto.Topic;
import org.springframework.util.Assert;

public enum GateToClientProtocol implements Protocol {
    PONG((short) 1),

    KICK_OUT((short) 2),

    PLAYER_TOKEN_VALIDATE((short) 3),

    // 获取服务器时间
    GET_SERVER_TIME((short) 4),

    ;

    private final short code;

    GateToClientProtocol(short code) {
        Assert.isTrue(code > 0, "协议号必须大于0");
        this.code = code;
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
}
