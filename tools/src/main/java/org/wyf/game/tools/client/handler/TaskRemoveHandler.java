package org.wyf.game.tools.client.handler;

import org.wyf.game.proto.AbstractMsgHandler;
import org.wyf.game.proto.protocol.LogicToClientProtocol;
import org.wyf.game.proto.protocol.Protocol;
import org.wyf.game.proto.struct.Task.PbTaskArrays;
import org.springframework.stereotype.Component;

@Component
public class TaskRemoveHandler extends AbstractMsgHandler<PbTaskArrays> {
    @Override
    public Protocol getProtocol() {
        return LogicToClientProtocol.TASK_REMOVE;
    }
}
