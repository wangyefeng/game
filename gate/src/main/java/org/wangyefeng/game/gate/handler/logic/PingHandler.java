package org.wangyefeng.game.gate.handler.logic;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wangyefeng.game.gate.handler.client.ClientMsgHandler;
import org.wangyefeng.game.gate.protocol.ClientProtocol;
import org.wangyefeng.game.gate.protocol.ToClientProtocol;

@Component
public class PingHandler implements ClientMsgHandler<Message> {

    private static final Logger log = LoggerFactory.getLogger(PingHandler.class);

    private static final ByteBuf PONG = Unpooled.unreleasableBuffer(Unpooled.directBuffer(6));


    static {
        PONG.writeInt(2);
        PONG.writeShort(ToClientProtocol.PONG.getCode());
    }

    @Override
    public void handle(Channel channel, Message message) {
        log.info("Received a ping message from client.");
        channel.writeAndFlush(PONG.duplicate());
    }

    @Override
    public ClientProtocol getProtocol() {
        return ClientProtocol.PING;
    }
}
