package org.wangyefeng.game.logic.net;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import org.wangyefeng.game.proto.Decoder;
import org.wangyefeng.game.proto.DecoderType;
import org.wangyefeng.game.proto.protocol.Protocol;
import org.wangyefeng.game.proto.protocol.ProtocolUtils;

public class ClientDecoder implements Decoder<ClientMsg<?>> {

    @Override
    public ClientMsg decode(ByteBuf msg) throws Exception {
        byte from = msg.readByte();
        short code = msg.readShort();
        int playerId = msg.readInt();
        int length = msg.readableBytes();
        Protocol protocol = ProtocolUtils.getProtocol(from, code);
        if (length > 0) {
            ByteBufInputStream inputStream = new ByteBufInputStream(msg);
            Message message = (Message) protocol.parser().parseFrom(inputStream);
            return new ClientMsg<>(playerId, protocol, message);
        } else {
            return new ClientMsg<>(playerId, protocol);
        }
    }

    @Override
    public DecoderType getType() {
        return DecoderType.MESSAGE_PLAYER;
    }
}
