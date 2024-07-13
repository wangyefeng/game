package org.wangyefeng.game.gate.net;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wangyefeng.game.gate.net.client.LogicClient;
import org.wangyefeng.game.gate.player.Player;
import org.wangyefeng.game.proto.DecoderType;
import org.wangyefeng.game.proto.MessageCode;
import org.wangyefeng.game.proto.Topic;
import org.wangyefeng.game.proto.protocol.Protocol;
import org.wangyefeng.game.proto.protocol.ProtocolUtils;

import java.util.List;

public class TcpCodec extends ByteToMessageCodec<MessageCode> {

    private static final Logger log = LoggerFactory.getLogger(TcpCodec.class);

    private LogicClient logicClient;

    public TcpCodec(LogicClient logicClient) {
        this.logicClient = logicClient;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageCode msg, ByteBuf out) throws Exception {
        if (msg.getMessage() != null) {
            out.writeInt(0);
            out.writeByte(DecoderType.MESSAGE_CODE.getCode());
            out.writeByte(msg.getProtocol().from().getCode());
            out.writeShort(msg.getProtocol().getCode());
            ByteBufOutputStream outputStream = new ByteBufOutputStream(out);
            msg.getMessage().writeTo(outputStream);
            out.setInt(0, out.readableBytes() - 4);
        } else {
            out.writeInt(4);
            out.writeByte(DecoderType.MESSAGE_CODE.getCode());
            out.writeByte(msg.getProtocol().from().getCode());
            out.writeShort(msg.getProtocol().getCode());
        }
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte from = Topic.CLIENT.getCode();
        byte type = in.readByte();
        if (type == DecoderType.MESSAGE_CODE.getCode()) {
            byte to = in.readByte();
            short code = in.readShort();
            Protocol protocol = ProtocolUtils.getProtocol(from, to, code);
            if (protocol == null || protocol.to().getCode() != to) {
                log.error("decode error, protocol not found, from: {}, to: {}, code: {}", from, to, code);
                in.skipBytes(in.readableBytes());
                return;
            }
            if (to == Topic.GATE.getCode()) { // 客户端发送gate处理的消息
                int length = in.readableBytes();
                if (length > 0) {
                    ByteBufInputStream inputStream = new ByteBufInputStream(in);
                    Message message = (Message) protocol.parser().parseFrom(inputStream);
                    out.add(new MessageCode<>(protocol, message));
                } else {
                    out.add(new MessageCode<>(protocol));
                }
            } else {
                Player player = ctx.channel().attr(AttributeKeys.PLAYER).get();
                if (player != null && logicClient.isRunning()) {
                    int readableBytes = in.readableBytes();
                    ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer(readableBytes + 12, readableBytes + 12);
                    buffer.writeInt(readableBytes + 8);
                    buffer.writeByte(DecoderType.MESSAGE_PLAYER.getCode());
                    buffer.writeByte(from);
                    buffer.writeShort(code);
                    buffer.writeInt(player.getId());
                    buffer.writeBytes(in);
                    logicClient.getChannel().writeAndFlush(buffer);
                } else {
                    log.error("handle message error, player not found, code: {}", code);
                    in.skipBytes(in.readableBytes());
                }
            }
        } else {
            log.error("decode error, illegal decoder type: {}", type);
            in.skipBytes(in.readableBytes());
        }
    }
}
