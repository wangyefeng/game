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
import org.wangyefeng.game.gate.protocol.ClientProtocol;
import org.wangyefeng.game.proto.DecoderType;
import org.wangyefeng.game.proto.MessageCode;

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
            out.writeShort(msg.getCode());
            ByteBufOutputStream outputStream = new ByteBufOutputStream(out);
            msg.getMessage().writeTo(outputStream);
            out.setInt(0, out.readableBytes() - 4);
        } else {
            out.writeInt(2);
            out.writeShort(msg.getCode());
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            short code = in.readShort();
            if (ClientProtocol.match(code)) { // 客户端发送gate处理的消息
                int length = in.readableBytes();
                if (length > 0) {
                    ByteBufInputStream inputStream = new ByteBufInputStream(in);
                    Message message = (Message) ClientProtocol.getParser(code).parseFrom(inputStream);
                    out.add(new MessageCode<>(code, message));
                } else {
                    out.add(new MessageCode<>(code));
                }
            } else {
                if (ctx.channel().hasAttr(AttributeKeys.PLAYER)) {
                    if (logicClient.isRunning()) {
                        int readableBytes = in.readableBytes();
                        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer(readableBytes + 11, readableBytes + 11);
                        buffer.writeInt(readableBytes + 7);
                        buffer.writeByte(DecoderType.MESSAGE_PLAYER.getCode());
                        buffer.writeShort(code);
                        buffer.writeInt(ctx.channel().attr(AttributeKeys.PLAYER).get().getId());
                        buffer.writeBytes(in);
                        logicClient.getChannel().writeAndFlush(buffer);
                    } else {
                        in.skipBytes(in.readableBytes());
                        log.error("handle message error, Logic server is not running, code: {}", code);
                    }
                }
            }
        } catch (Exception e) {
            log.error("decode error", e);
            in.skipBytes(in.readableBytes());
        }
    }
}
