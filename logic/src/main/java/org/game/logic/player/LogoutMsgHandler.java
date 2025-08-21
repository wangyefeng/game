package org.game.logic.player;

import com.google.protobuf.Empty;
import org.game.proto.protocol.GateToLogicProtocol;
import org.springframework.stereotype.Component;

@Component
public class LogoutMsgHandler extends PlayerHandler<Empty> {

    @Override
    protected void handle(Player player, Empty message) {
        if (player.isOffline()) {
            return;
        }
        player.logout();
    }

    @Override
    public GateToLogicProtocol getProtocol() {
        return GateToLogicProtocol.LOGOUT;
    }
}
