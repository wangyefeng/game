package org.game.client.handler;

import org.game.proto.MsgHandler;
import org.game.proto.protocol.LogicToClientProtocol;
import org.game.proto.protocol.Protocol;
import org.game.proto.struct.Login.PbLoginResp;

public class LoginHandler implements MsgHandler<PbLoginResp> {
    @Override
    public Protocol getProtocol() {
        return LogicToClientProtocol.LOGIN;
    }
}
