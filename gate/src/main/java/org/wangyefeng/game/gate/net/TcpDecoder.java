package org.wangyefeng.game.gate.net;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.timeout.ReadTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wangyefeng.game.gate.net.client.LogicClient;
import org.wangyefeng.game.gate.protocol.ClientProtocol;

import java.net.SocketException;
import java.util.List;

public class TcpDecoder extends ByteToMessageDecoder {

    private static final Logger log = LoggerFactory.getLogger(TcpDecoder.class);


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("Channel active: {}", ctx.channel());
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

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof SocketException) {
            log.info("Socket exception {} channel: {}", cause.getMessage(), ctx.channel());
        } else if (cause instanceof ReadTimeoutException) {
            log.info("Read timeout: {}", ctx.channel());
        } else {
            log.error("Exception caught in channel: {}", ctx.channel(), cause);
            ctx.close();
        }
    }
}
