package org.game.logic.player;

import com.google.protobuf.Empty;
import org.game.config.Configs;
import org.game.logic.net.ChannelKeys;
import org.game.proto.protocol.GateToLogicProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogoutMsgHandler extends PlayerHandler<Empty> {

    private static final Logger log = LoggerFactory.getLogger(LogoutMsgHandler.class);

    @Override
    protected void handle(Player player, Empty message, Configs config) {
        log.info("玩家{}退出游戏", player.getId());
        Players.removePlayer(player.getId());
        player.getChannel().attr(ChannelKeys.PLAYERS_KEY).get().remove((Integer) player.getId());
        player.logout();
    }

    @Override
    public GateToLogicProtocol getProtocol() {
        return GateToLogicProtocol.LOGOUT;
    }
}
