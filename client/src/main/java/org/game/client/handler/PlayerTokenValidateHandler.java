package org.game.client.handler;

import org.game.proto.MsgHandler;
import org.game.proto.protocol.GateToClientProtocol;
import org.game.proto.protocol.Protocol;
import org.game.proto.struct.Login.PbAuthResp;

public class PlayerTokenValidateHandler implements MsgHandler<PbAuthResp> {
    @Override
    public Protocol getProtocol() {
        return GateToClientProtocol.PLAYER_TOKEN_VALIDATE;
    }
}
