package org.wyf.game.tools.client.handler;

import com.google.protobuf.Empty;
import org.wyf.game.proto.AbstractMsgHandler;
import org.wyf.game.proto.protocol.GateToClientProtocol;
import org.wyf.game.proto.protocol.Protocol;
import org.springframework.stereotype.Component;

@Component
public class PongHandler extends AbstractMsgHandler<Empty> {
    @Override
    public Protocol getProtocol() {
        return GateToClientProtocol.PONG;
    }
}
