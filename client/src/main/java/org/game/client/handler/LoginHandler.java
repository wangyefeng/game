package org.game.client.handler;

import org.game.proto.AbstractMsgHandler;
import org.game.proto.protocol.LogicToClientProtocol;
import org.game.proto.protocol.Protocol;
import org.game.proto.struct.Login.PbLoginResp;
import org.springframework.stereotype.Component;

@Component
public class LoginHandler extends AbstractMsgHandler<PbLoginResp> {
    @Override
    public Protocol getProtocol() {
        return LogicToClientProtocol.LOGIN;
    }
}
