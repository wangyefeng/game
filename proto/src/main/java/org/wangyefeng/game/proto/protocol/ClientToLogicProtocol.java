package org.wangyefeng.game.proto.protocol;

import com.google.protobuf.Parser;
import org.wangyefeng.game.proto.Topic;
import org.wangyefeng.game.proto.struct.Common;

public enum ClientToLogicProtocol implements Protocol {
    LOGIN((short) 1, Common.PbInt.parser()),

    TEST((short) 2);


    private final short code;

    private final Parser<?> parser;

    ClientToLogicProtocol(short code) {
        this(code, null);
    }

    ClientToLogicProtocol(short code, Parser<?> parser) {
        this.code = code;
        this.parser = parser;
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

    @Override
    public Parser<?> parser() {
        return parser;
    }
}
