package org.game.proto.protocol;

import org.game.proto.Topic;
import org.springframework.util.Assert;

public enum LogicToClientProtocol implements Protocol {

    LOGIN((short) 1),

    TASK_UPDATE((short) 2),

    TASK_ADD((short) 3),

    TASK_REMOVE((short) 4),

    ;

    private final short code;

    LogicToClientProtocol(short code) {
        Assert.isTrue(code > 0, "协议号必须大于0");
        this.code = code;
    }

    @Override
    public Topic from() {
        return Topic.LOGIC;
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
