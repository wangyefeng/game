package org.game.logic.player;

import org.game.logic.data.config.CfgItemService;
import org.game.logic.data.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.game.proto.protocol.ClientToLogicProtocol;
import org.game.proto.protocol.LogicToClientProtocol;
import org.game.proto.struct.Common;

@Component
public class TestHandler extends PlayerHandler<Common.PbInt> {

    private static final Logger log = LoggerFactory.getLogger(TestHandler.class);

    @Override
    protected void handle(Player player, Common.PbInt message, Config config) {
        log.info("TestHandler {} received message: {}", player.getPlayerInfo().getName(), message);
        CfgItemService cfgItemService = config.get(CfgItemService.class);
        player.sendToClient(LogicToClientProtocol.TEST, Common.PbInt.newBuilder().setVal(cfgItemService.getCfg(1).getId()).build());
    }

    @Override
    public ClientToLogicProtocol getProtocol() {
        return ClientToLogicProtocol.TEST;
    }
}
