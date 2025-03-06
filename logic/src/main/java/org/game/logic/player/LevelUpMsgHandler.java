package org.game.logic.player;

import org.game.config.Configs;
import org.game.proto.protocol.ClientToLogicProtocol;
import org.game.proto.struct.Common.PbInt;
import org.springframework.stereotype.Component;

@Component
public class LevelUpMsgHandler extends PlayerHandler<PbInt> {

    @Override
    protected void handle(Player player, PbInt data, Configs config) {
        PlayerService playerService = player.getService(PlayerService.class);
        playerService.levelUp();
    }

    @Override
    public ClientToLogicProtocol getProtocol() {
        return ClientToLogicProtocol.LEVEL_UP;
    }
}
