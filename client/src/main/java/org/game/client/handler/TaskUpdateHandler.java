package org.game.client.handler;

import org.game.proto.MsgHandler;
import org.game.proto.protocol.LogicToClientProtocol;
import org.game.proto.protocol.Protocol;
import org.game.proto.struct.Task.PbTask;

public class TaskUpdateHandler implements MsgHandler<PbTask> {
    @Override
    public Protocol getProtocol() {
        return LogicToClientProtocol.TASK_UPDATE;
    }
}
