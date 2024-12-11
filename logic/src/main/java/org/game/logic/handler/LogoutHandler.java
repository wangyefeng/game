package org.game.logic.handler;

import io.netty.channel.Channel;
import org.game.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.game.logic.player.PlayerService;
import org.game.logic.player.Players;
import org.game.logic.thread.ThreadPool;
import org.game.proto.protocol.GateToLogicProtocol;
import org.game.proto.struct.Common;

@Component
public class LogoutHandler implements GateMsgHandler<Common.PbInt> {

    @Autowired
    private PlayerService playerService;

    private static final Logger log = LoggerFactory.getLogger(LogoutHandler.class);


    @Override
    public void handle(Channel channel, Common.PbInt msg, Config config) {
        ThreadPool.getPlayerExecutor(msg.getVal()).execute(() -> {
            log.info("gate通知logic玩家{}退出", msg.getVal());
            playerService.logout(Players.getPlayer(msg.getVal()));
        });
    }

    @Override
    public GateToLogicProtocol getProtocol() {
        return GateToLogicProtocol.LOGOUT;
    }
}
