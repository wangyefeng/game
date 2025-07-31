package org.game.client.handler;

import org.game.proto.AbstractMsgHandler;
import org.game.proto.protocol.GateToClientProtocol;
import org.game.proto.protocol.Protocol;
import org.game.proto.struct.Login.PbAuthResp;
import org.springframework.stereotype.Component;

@Component
public class PlayerTokenValidateHandler extends AbstractMsgHandler<PbAuthResp> {
    @Override
    public Protocol getProtocol() {
        return GateToClientProtocol.PLAYER_TOKEN_VALIDATE;
    }
}
