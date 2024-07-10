package org.wangyefeng.game.gate.handler.client;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wangyefeng.game.gate.handler.logic.LogicMsgHandler;
import org.wangyefeng.game.gate.protocol.LogicProtocol;

@Component
public class PongHandler implements LogicMsgHandler<Message> {

    private static final Logger log = LoggerFactory.getLogger(PongHandler.class);

    @Override
    public void handle(Channel channel, Message message) {
        log.debug("Received a pong message from logic.");
    }

    @Override
    public LogicProtocol getProtocol() {
        return LogicProtocol.PONG;
    }
}
