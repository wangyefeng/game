package org.game.client.handler;

import com.google.protobuf.Empty;
import org.game.proto.MsgHandler;
import org.game.proto.protocol.GateToClientProtocol;
import org.game.proto.protocol.Protocol;

public class PongHandler implements MsgHandler<Empty> {
    @Override
    public Protocol getProtocol() {
        return GateToClientProtocol.PONG;
    }
}
