package org.game.logic.player;

import com.google.protobuf.Empty;
import org.game.config.Configs;
import org.game.proto.protocol.ClientToLogicProtocol;
import org.springframework.stereotype.Component;

@Component
public class LevelUpMsgHandler extends PlayerHandler<Empty> {

    @Override
    protected void handle(Player player, Empty data) {
        PlayerService playerService = player.getService(PlayerService.class);
        playerService.levelUp();
    }

    @Override
    public ClientToLogicProtocol getProtocol() {
        return ClientToLogicProtocol.LEVEL_UP;
    }
}
