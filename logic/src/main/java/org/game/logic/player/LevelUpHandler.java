package org.game.logic.player;

import org.game.config.Configs;
import org.game.proto.protocol.ClientToLogicProtocol;
import org.game.proto.struct.Common.PbInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LevelUpHandler extends PlayerHandler<PbInt> {

    private static final Logger log = LoggerFactory.getLogger(LevelUpHandler.class);

    @Override
    protected void handle(Player player, PbInt message, Configs config) {
        PlayerService playerService = player.getService(PlayerService.class);
        playerService.levelUp();
    }

    @Override
    public ClientToLogicProtocol getProtocol() {
        return ClientToLogicProtocol.LEVEL_UP;
    }
}
