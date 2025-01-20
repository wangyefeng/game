package org.game.gate.handler.logic;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.game.proto.protocol.LogicToGateProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PongHandler implements LogicMsgHandler<Message> {

    private static final Logger log = LoggerFactory.getLogger(PongHandler.class);

    @Override
    public void handle(Channel channel, Message message) {
        log.debug("Received a pong message from logic.");
    }

    @Override
    public LogicToGateProtocol getProtocol() {
        return LogicToGateProtocol.PING;
    }
}
