package org.wangyefeng.game.logic.player;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wangyefeng.game.logic.handler.ClientHandler;
import org.wangyefeng.game.logic.protocol.ClientProtocol;
import org.wangyefeng.game.proto.Common;

@Component
public class LoginHandler implements ClientHandler<Common.PbInt> {


    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public void handle(Channel channel, int playerId, Common.PbInt message) {
        log.info("LoginHandler: playerId: {}, message: {}", playerId, message);
    }

    @Override
    public ClientProtocol getProtocol() {
        return ClientProtocol.LOGIN;
    }
}
