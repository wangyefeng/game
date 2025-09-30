package org.wyf.game.proto.protocol;

import org.wyf.game.proto.Topic;
import org.springframework.util.Assert;

public enum ClientToLogicProtocol implements Protocol {

    LOGIN((short) 1),

    REGISTER((short) 2),

    LEVEL_UP((short)3),

    ;
    private final short code;

    ClientToLogicProtocol(short code) {
        Assert.isTrue(code > 0, "协议号必须大于0");
        this.code = code;
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

}
