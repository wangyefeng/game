package org.game.proto;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import org.game.proto.protocol.Protocol;
import org.game.proto.protocol.Protocols;
import org.springframework.util.Assert;

/**
 * 消息码解码器
 */
public class MessagePlayerDecoder implements Decoder {

    private final MsgHandlerFactory msgHandlerFactory;

    public MessagePlayerDecoder(MsgHandlerFactory msgHandlerFactory) {
        this.msgHandlerFactory = msgHandlerFactory;
    }

    @Override
    public Object decode(ByteBuf msg, byte to) throws Exception {
        byte from = msg.readByte();
        int playerId = msg.readInt();
        short code = msg.readShort();
        Protocol protocol = Protocols.getProtocol(from, to, code);
        Assert.notNull(protocol, "No protocol found for from: " + from + ", to: " + to + ", code: " + code);
        ByteBufInputStream inputStream = new ByteBufInputStream(msg);
        Message message = msgHandlerFactory.getHandler(protocol).parseFrom(inputStream);
        return MessagePlayer.of(playerId, protocol, message);
    }

    @Override
    public DecoderType getType() {
        return DecoderType.MESSAGE_PLAYER;
    }
}
