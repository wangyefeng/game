package org.wyf.game.tools.client.handler;

import org.wyf.game.proto.AbstractMsgHandler;
import org.wyf.game.proto.protocol.LogicToClientProtocol;
import org.wyf.game.proto.protocol.Protocol;
import org.wyf.game.proto.struct.Login;
import org.springframework.stereotype.Component;

@Component
public class LoginHandler extends AbstractMsgHandler<Login.PbLoginOrRegisterResp> {
    @Override
    public Protocol getProtocol() {
        return LogicToClientProtocol.LOGIN;
    }
}
