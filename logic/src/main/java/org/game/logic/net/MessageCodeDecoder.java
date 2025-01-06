package org.game.logic.net;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import jakarta.transaction.NotSupportedException;
import org.game.proto.Decoder;
import org.game.proto.DecoderType;
import org.game.proto.MessageCode;
import org.game.proto.Topic;
import org.game.proto.protocol.Protocol;
import org.game.proto.protocol.Protocols;
import org.springframework.util.Assert;

/**
 * 消息码解码器
 */
public class MessageCodeDecoder implements Decoder<Object> {

    public MessageCodeDecoder() {
    }

    @Override
    public Object decode(ByteBuf msg, byte to) throws Exception {
        byte from = msg.readByte();
        short code = msg.readShort();
        Protocol protocol = Protocols.getProtocol(from, to, code);
        Assert.notNull(protocol, "No protocol found for from: " + from + ", to: " + to + ", code: " + code);
        MessageCode<?> messageCode;
        if (protocol.parser() != null) {
            ByteBufInputStream inputStream = new ByteBufInputStream(msg);
            Message message = (Message) protocol.parser().parseFrom(inputStream);
            messageCode = new MessageCode<>(protocol, message);
        } else {
            messageCode = new MessageCode<>(protocol);
        }
        if (protocol.from().equals(Topic.CLIENT)) {
            return new ClientMessage<>(messageCode);
        } else if (protocol.from().equals(Topic.GATE)) {
            return new GateMessage<>(messageCode);
        } else {
            throw new NotSupportedException("Unsupported topic: " + protocol.from());
        }
    }

    @Override
    public DecoderType getType() {
        return DecoderType.MESSAGE_CODE;
    }
}
