package org.wangyefeng.game.gate.handler.logic;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wangyefeng.game.proto.protocol.GateToClientProtocol;
import org.wangyefeng.game.proto.protocol.Protocol;

@Component
public class PongHandler implements LogicMsgHandler<Message> {

    private static final Logger log = LoggerFactory.getLogger(PongHandler.class);

    @Override
    public void handle(Channel channel, Message message) {
        log.info("Received a pong message from logic.");
    }

    @Override
    public Protocol getProtocol() {
        return GateToClientProtocol.PONG;
    }
}
