package org.game.client.handler;

import com.google.protobuf.Empty;
import org.game.proto.AbstractMsgHandler;
import org.game.proto.protocol.GateToClientProtocol;
import org.game.proto.protocol.Protocol;
import org.springframework.stereotype.Component;

@Component
public class PongHandler extends AbstractMsgHandler<Empty> {
    @Override
    public Protocol getProtocol() {
        return GateToClientProtocol.PONG;
    }
}
