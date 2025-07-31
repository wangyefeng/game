package org.game.client.handler;

import org.game.proto.AbstractMsgHandler;
import org.game.proto.protocol.LogicToClientProtocol;
import org.game.proto.protocol.Protocol;
import org.game.proto.struct.Task.PbTaskArrays;
import org.springframework.stereotype.Component;

@Component
public class TaskAddHandler extends AbstractMsgHandler<PbTaskArrays> {
    @Override
    public Protocol getProtocol() {
        return LogicToClientProtocol.TASK_ADD;
    }
}
