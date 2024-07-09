package org.wangyefeng.game.gate.handler.client;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wangyefeng.game.gate.net.AttributeKeys;
import org.wangyefeng.game.gate.protocol.ClientProtocol;
import org.wangyefeng.game.proto.Common;

@Component
public class TokenValidateHandler implements ClientMsgHandler<Common.PbInt> {

    private static final Logger log = LoggerFactory.getLogger(TokenValidateHandler.class);

    @Override
    public void handle(Channel channel, Common.PbInt msg) {
        if (channel.hasAttr(AttributeKeys.PLAYER_ID)) {
            log.warn("Player {} has already logged in.", channel.attr(AttributeKeys.PLAYER_ID).get());
            return;
        }
        // TODO: validate token
    }

    @Override
    public ClientProtocol getProtocol() {
        return ClientProtocol.TOKEN_VALIDATE;
    }
}
