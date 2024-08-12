package org.game.gate.handler.client;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.game.proto.DecoderType;
import org.game.proto.Topic;
import org.game.proto.protocol.ClientToGateProtocol;
import org.game.proto.protocol.GateToClientProtocol;

@Component
public final class PingHandler implements ClientMsgHandler<Message> {

    private static final Logger log = LoggerFactory.getLogger(PingHandler.class);

    private static final ByteBuf PONG = Unpooled.unreleasableBuffer(Unpooled.directBuffer(8, 8));


    static {
        PONG.writeInt(4);
        PONG.writeByte(DecoderType.MESSAGE_CODE.getCode());
        PONG.writeByte(Topic.GATE.getCode());
        PONG.writeShort(GateToClientProtocol.PONG.getCode());
    }

    @Override
    public void handle(Channel channel, Message message) {
        log.debug("Received a ping message from client.");
        channel.writeAndFlush(PONG.duplicate());
    }

    @Override
    public ClientToGateProtocol getProtocol() {
        return ClientToGateProtocol.PING;
    }
}
