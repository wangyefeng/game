package org.game.gate.handler.client;

import com.google.protobuf.Message;
import org.game.proto.CodeMsgHandler;
import org.game.proto.protocol.ClientToGateProtocol;

public interface ClientMsgHandler<T extends Message> extends CodeMsgHandler<T> {

    @Override
    ClientToGateProtocol getProtocol();

}
