package org.game.logic.gate;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.game.config.Configs;
import org.game.logic.net.GateMsgHandler;
import org.game.proto.protocol.GateToLogicProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PingHandler implements GateMsgHandler<Message> {

    private static final Logger log = LoggerFactory.getLogger(PingHandler.class);

    @Override
    public void handle(Channel channel, Message msg, Configs config) {
        log.debug("Received a ping message from gate.");
        // do nothing
    }

    @Override
    public GateToLogicProtocol getProtocol() {
        return GateToLogicProtocol.PING;
    }
}
