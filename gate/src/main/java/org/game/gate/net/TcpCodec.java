package org.game.gate.net;

import com.google.protobuf.Message;
import io.netty.buffer.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.game.gate.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.game.gate.net.client.LogicClient;
import org.game.proto.DecoderType;
import org.game.proto.MessageCode;
import org.game.proto.Topic;
import org.game.proto.protocol.Protocol;
import org.game.proto.protocol.Protocols;

import java.util.List;

public class TcpCodec extends ByteToMessageCodec<MessageCode> {

    private static final Logger log = LoggerFactory.getLogger(TcpCodec.class);

    private LogicClient logicClient;

    private LeakyBucket leakyBucket = new LeakyBucket(20, 10);

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
        if (!leakyBucket.addRequest()) {
            if (ctx.channel().isOpen()) {
                log.warn("触发限流，channel: {}", ctx.channel());
                ctx.close();
            }
            return;
        }
        byte from = Topic.CLIENT.getCode();
        byte type = in.readByte();
        if (type == DecoderType.MESSAGE_CODE.getCode()) {
            byte to = in.readByte();
            short code = in.readShort();
            Protocol protocol = Protocols.getProtocol(from, to, code);
            if (protocol == null || protocol.to().getCode() != to) {
                log.error("decode error, protocol not found, from: {}, to: {}, code: {}", from, to, code);
                in.skipBytes(in.readableBytes());
                return;
            }
            if (to == Topic.GATE.getCode()) { // 客户端发送gate处理的消息
                if (protocol.parser() != null) {
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
                    ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer(12, 12);
                    try {
                        buffer.writeInt(readableBytes + 8);
                        buffer.writeByte(DecoderType.MESSAGE_PLAYER.getCode());
                        buffer.writeByte(from);
                        buffer.writeInt(player.getId());
                        buffer.writeShort(code);
                    } catch (Exception e) {
                        buffer.release();
                        throw e;
                    }
                    ByteBuf byteBuf = new CompositeByteBuf(PooledByteBufAllocator.DEFAULT, true, 2, buffer, in.retainedDuplicate());
                    logicClient.getChannel().writeAndFlush(byteBuf);
                    in.skipBytes(in.readableBytes());
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
