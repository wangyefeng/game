package org.game.logic.handler;

import org.game.config.Config;
import org.game.logic.player.Player;
import org.game.proto.protocol.ClientToLogicProtocol;
import org.game.proto.protocol.LogicToClientProtocol;
import org.game.proto.struct.Common;
import org.game.proto.struct.Common.PbInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TestHandler extends PlayerHandler<PbInt> {

    private static final Logger log = LoggerFactory.getLogger(TestHandler.class);

    @Override
    protected void handle(Player player, Common.PbInt message, Config config) {
        player.sendToClient(LogicToClientProtocol.TEST, message);
    }

    @Override
    public ClientToLogicProtocol getProtocol() {
        return ClientToLogicProtocol.TEST;
    }
}
