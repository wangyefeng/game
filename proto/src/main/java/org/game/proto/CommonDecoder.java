package org.game.proto;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonDecoder extends ByteToMessageDecoder {

    private static final Logger log = LoggerFactory.getLogger(CommonDecoder.class);

    private final byte to;

    private Map<Byte, Decoder> decoders = new HashMap<>();

    public CommonDecoder(byte to) {
        this.to = to;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        try {
            in.markReaderIndex();
            byte type = in.readByte();
            Decoder decoder = decoders.get(type);
            if (decoder == null) {
                throw new IllegalArgumentException("非法协议类型: " + type);
            }
            Object message = decoder.decode(in, to);
            out.add(message);
        } catch (Exception e) {
            in.resetReaderIndex();
            byte[] bytes = new byte[in.readableBytes()];
            in.readBytes(bytes);
            log.error("解码失败，字节码信息：{}", bytes, e);
        }
    }

    public void registerDecoder(Decoder decoder) {
        if (decoders.containsKey(decoder.getType().getCode())) {
            throw new IllegalArgumentException("Duplicate message type: " + decoder.getType().getCode());
        }
        decoders.put(decoder.getType().getCode(), decoder);
    }
}
