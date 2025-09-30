package org.wyf.game.proto.protocol;

import org.wyf.game.proto.Topic;
import org.springframework.util.Assert;

public enum LogicToGateProtocol implements Protocol {
    PING((short) 1),

    KICK_OUT((short) 2),
    ;

    private final short code;

    LogicToGateProtocol(short code) {
        Assert.isTrue(code > 0, "协议号必须大于0");
        this.code = code;
    }

    @Override
    public Topic from() {
        return Topic.LOGIC;
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
