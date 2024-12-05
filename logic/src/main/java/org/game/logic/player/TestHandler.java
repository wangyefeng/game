package org.game.logic.player;

import org.game.logic.data.config.Config;
import org.game.proto.protocol.ClientToLogicProtocol;
import org.game.proto.protocol.LogicToClientProtocol;
import org.game.proto.struct.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TestHandler extends PlayerHandler<Common.PbInt> {

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
