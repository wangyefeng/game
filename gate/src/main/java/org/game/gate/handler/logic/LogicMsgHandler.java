package org.game.gate.handler.logic;

import com.google.protobuf.Message;
import org.game.proto.CodeMsgHandler;
import org.game.proto.protocol.LogicToGateProtocol;

public interface LogicMsgHandler<T extends Message> extends CodeMsgHandler<T> {

    @Override
    LogicToGateProtocol getProtocol();
}
