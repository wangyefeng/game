package org.wyf.game.logic.player;

import com.google.protobuf.Empty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wyf.game.proto.protocol.ClientToLogicProtocol;
import org.springframework.stereotype.Component;

@Component
public class LevelUpMsgHandler extends PlayerHandler<Empty> {

    private static final Logger log = LoggerFactory.getLogger(LevelUpMsgHandler.class);

    @Override
    protected void handle(Player player, Empty data) {
        PlayerService playerService = player.getService(PlayerService.class);
        playerService.levelUp();
        log.info("player {} level up to {}", player.getId(), playerService.getEntity().getLevel());
    }

    @Override
    public ClientToLogicProtocol getProtocol() {
        return ClientToLogicProtocol.LEVEL_UP;
    }
}
