package org.wangyefeng.game.logic.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wangyefeng.game.logic.data.Player;
import org.wangyefeng.game.proto.protocol.ClientToLogicProtocol;
import org.wangyefeng.game.proto.struct.Common;

@Component
public class TestHandler extends AbstractPlayerHandler<Common.PbInt> {

    private static final Logger log = LoggerFactory.getLogger(TestHandler.class);

    @Override
    protected void handle(Player player, Common.PbInt message) {
        log.info("TestHandler {} received message: {}", player.getName(), message);
    }

    @Override
    public ClientToLogicProtocol getProtocol() {
        return ClientToLogicProtocol.TEST;
    }
}
