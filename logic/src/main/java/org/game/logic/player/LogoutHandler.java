package org.game.logic.player;

import io.netty.channel.Channel;
import org.game.config.Configs;
import org.game.logic.net.GateHandler;
import org.game.logic.net.GateMsgHandler;
import org.game.logic.thread.ThreadPool;
import org.game.proto.protocol.GateToLogicProtocol;
import org.game.proto.struct.Common;
import org.game.proto.struct.Common.PbInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogoutHandler implements GateMsgHandler<PbInt> {

    private static final Logger log = LoggerFactory.getLogger(LogoutHandler.class);


    @Override
    public void handle(Channel channel, Common.PbInt data, Configs config) {
        ThreadPool.getPlayerExecutor(data.getVal()).execute(() -> {
            Player player = Players.getPlayer(data.getVal());
            if (player == null) {
                log.info("玩家{}退出游戏，但玩家不在线", data.getVal());
                return;
            }
            player.logout();
            Players.removePlayer(data.getVal());
            log.info("玩家{}退出游戏", data.getVal());
            channel.attr(GateHandler.PLAYERS_KEY).get().remove((Integer) player.getId());
        });
    }

    @Override
    public GateToLogicProtocol getProtocol() {
        return GateToLogicProtocol.LOGOUT;
    }
}
