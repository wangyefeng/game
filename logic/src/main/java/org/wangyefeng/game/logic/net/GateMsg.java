package org.wangyefeng.game.logic.net;

import com.google.protobuf.Message;
import org.wangyefeng.game.proto.MessageCode;
import org.wangyefeng.game.proto.protocol.Protocol;

public class GateMsg<T extends Message> extends MessageCode<T> {

    public GateMsg(Protocol protocol, T message) {
        super(protocol, message);
    }

    public GateMsg(Protocol protocol) {
        super(protocol);
    }
}
