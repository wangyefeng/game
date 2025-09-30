package org.wyf.game.proto.protocol;

import org.wyf.game.proto.Topic;
import org.springframework.util.Assert;

public enum GateToLogicProtocol implements Protocol {
    PING((short) 1),

    LOGOUT((short) 2),
    ;


    private final short code;

    GateToLogicProtocol(short code) {
        Assert.isTrue(code > 0, "协议号必须大于0");
        this.code = code;
    }

    @Override
    public Topic from() {
        return Topic.GATE;
    }

    @Override
    public Topic to() {
        return Topic.LOGIC;
    }

    @Override
    public short getCode() {
        return code;
    }
}
