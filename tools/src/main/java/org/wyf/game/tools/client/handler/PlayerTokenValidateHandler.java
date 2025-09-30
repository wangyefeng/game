package org.wyf.game.tools.client.handler;

import org.wyf.game.proto.AbstractMsgHandler;
import org.wyf.game.proto.protocol.GateToClientProtocol;
import org.wyf.game.proto.protocol.Protocol;
import org.wyf.game.proto.struct.Login.PbAuthResp;
import org.springframework.stereotype.Component;

@Component
public class PlayerTokenValidateHandler extends AbstractMsgHandler<PbAuthResp> {
    @Override
    public Protocol getProtocol() {
        return GateToClientProtocol.PLAYER_TOKEN_VALIDATE;
    }
}
