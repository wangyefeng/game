package org.game.proto.protocol;

import com.google.protobuf.Parser;
import org.game.proto.Topic;
import org.game.proto.struct.Common;
import org.game.proto.struct.Login;
import org.game.proto.struct.Task;
import org.springframework.util.Assert;

public enum LogicToClientProtocol implements Protocol {

    LOGIN((short) 1, Login.PbLoginResp.parser()),

    TASK_UPDATE((short) 2, Task.PbTask.parser()),

    TASK_ADD((short) 3, Task.PbTaskArrays.parser()),

    TASK_REMOVE((short) 4, Common.PbIntArray.parser()),

    ;

    private final short code;

    private final Parser<?> parser;

    LogicToClientProtocol(short code) {
        this(code, null);
    }

    LogicToClientProtocol(short code, Parser<?> parser) {
        Assert.isTrue(code > 0, "协议号必须大于0");
        this.code = code;
        this.parser = parser;
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

    @Override
    public Parser<?> parser() {
        return parser;
    }
}
