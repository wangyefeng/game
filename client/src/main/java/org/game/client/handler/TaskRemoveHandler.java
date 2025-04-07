package org.game.client.handler;

import org.game.proto.MsgHandler;
import org.game.proto.protocol.LogicToClientProtocol;
import org.game.proto.protocol.Protocol;
import org.game.proto.struct.Task.PbTaskArrays;

public class TaskRemoveHandler implements MsgHandler<PbTaskArrays> {
    @Override
    public Protocol getProtocol() {
        return LogicToClientProtocol.TASK_REMOVE;
    }
}
