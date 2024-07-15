package org.wangyefeng.game.gate.handler.client;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wangyefeng.game.gate.player.Player;
import org.wangyefeng.game.proto.protocol.ClientToGateProtocol;

@Component
public class TestHandler extends AbstractPlayerMsgHandler<Message> {

    private static final Logger log = LoggerFactory.getLogger(TestHandler.class);

    @Override
    protected void handle(Channel channel, Message message, Player player) throws Exception {
        log.info("test handler player: {}", player.getId());
//        Thread.sleep(150);
    }

    @Override
    public ClientToGateProtocol getProtocol() {
        return ClientToGateProtocol.TEST;
    }
}
