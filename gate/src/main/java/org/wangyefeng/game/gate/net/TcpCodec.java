package org.wangyefeng.game.gate.net;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wangyefeng.game.gate.net.client.LogicClient;
import org.wangyefeng.game.gate.protocol.ClientProtocol;

import java.util.List;

public class TcpCodec extends ByteToMessageCodec<ClientMessage> {

    private static final Logger log = LoggerFactory.getLogger(TcpCodec.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, ClientMessage msg, ByteBuf out) throws Exception {
        if (msg.getMessage() != null) {
            out.writeInt(0);
            out.writeShort(msg.getCode());
            ByteBufOutputStream outputStream = new ByteBufOutputStream(out);
            msg.getMessage().writeTo(outputStream);
            out.setInt(0, out.readableBytes() - 4);
        } else {
            out.writeInt(4);
            out.writeShort(msg.getCode());
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        short code = in.readShort();
        if (ClientProtocol.match(code)) { // 客户端发送gate处理的消息
            int length = in.readableBytes();
            if (length > 0) {
                ByteBufInputStream inputStream = new ByteBufInputStream(in);
                Message message = (Message) ClientProtocol.getParser(code).parseFrom(inputStream);
                out.add(new ClientMessage<>(code, message));
            } else {
                out.add(new ClientMessage<>(code));
            }
        } else {
            LogicClient client = LogicClient.getInstance();
            if (client.isRunning()) {
                in.writeInt(1);
                client.getChannel().writeAndFlush(in.retain());
            } else {
                log.error("Logic client not running, discard message: {}", in);
            }
        }
    }
}
