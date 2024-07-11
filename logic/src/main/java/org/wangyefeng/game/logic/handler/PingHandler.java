package org.wangyefeng.game.logic.handler;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wangyefeng.game.logic.net.TcpServer;
import org.wangyefeng.game.logic.protocol.GateProtocol;
import org.wangyefeng.game.logic.protocol.ToGateProtocol;
import org.wangyefeng.game.proto.DecoderType;

@Component
public class PingHandler implements GateMsgHandler<Message> {

    private static final Logger log = LoggerFactory.getLogger(PingHandler.class);

    private static final ByteBuf PONG = Unpooled.unreleasableBuffer(Unpooled.directBuffer(TcpServer.MIN_FRAME_LENGTH));

    static {
        PONG.writeInt(TcpServer.PROTOCOL_TYPE_LENGTH + TcpServer.CODE_LENGTH);
        PONG.writeByte(DecoderType.MESSAGE_CODE.getCode());
        PONG.writeShort(ToGateProtocol.PONG.getCode());
    }

    @Override
    public void handle(Channel channel, Message message) {
        log.info("Received a ping message from gate.");
        channel.writeAndFlush(PONG.duplicate());// 回应PONG消息
    }

    @Override
    public GateProtocol getProtocol() {
        return GateProtocol.PING;
    }
}
