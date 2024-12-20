package org.game.gate.net;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.game.gate.player.Player;
import org.game.proto.DecoderType;
import org.game.proto.MessageCode;
import org.game.proto.Topic;
import org.game.proto.protocol.Protocol;
import org.game.proto.protocol.Protocols;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TcpCodec extends ByteToMessageCodec<MessageCode> {

    private static final Logger log = LoggerFactory.getLogger(TcpCodec.class);

    private LeakyBucket leakyBucket = new LeakyBucket(20, 10);

    public TcpCodec() {
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
            log.warn("触发限流，channel: {}", ctx.channel());
            ctx.close();
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
            if (to == Topic.GATE.getCode()) { // gate
                if (protocol.parser() != null) {
                    ByteBufInputStream inputStream = new ByteBufInputStream(in);
                    Message message = (Message) protocol.parser().parseFrom(inputStream);
                    out.add(new MessageCode<>(protocol, message));
                } else {
                    out.add(new MessageCode<>(protocol));
                }
            } else if (to == Topic.LOGIC.getCode()) {// logic
                Player player = ctx.channel().attr(AttributeKeys.PLAYER).get();
                if (player != null && player.getLogicClient().isRunning()) {
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
                    player.getLogicClient().getChannel().writeAndFlush(byteBuf);
                    in.skipBytes(in.readableBytes());
                } else {
                    if (player == null) {
                        log.error("handle message error, player not found, code: {}", code);
                    } else {
                        log.error("handle message error, logic not running, code: {}", code);
                    }
                    in.skipBytes(in.readableBytes());
                }
            } else {
                log.warn("decode error, illegal to topic: {}, code: {}", to, code);
                ctx.close();
            }
        } else {
            log.error("decode error, illegal decoder type: {}", type);
            ctx.close();
        }
    }
}
