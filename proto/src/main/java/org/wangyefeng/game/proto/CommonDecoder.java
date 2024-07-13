package org.wangyefeng.game.proto;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonDecoder extends ByteToMessageDecoder {

    private final byte to;

    private Map<Byte, Decoder<?>> decoders = new HashMap<>();

    public CommonDecoder(byte to) {
        this.to = to;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte type = in.readByte();
        Decoder<?> decoder = decoders.get(type);
        if (decoder == null) {
            throw new IllegalArgumentException("Unknown message type: " + type);
        }
        out.add(decoder.decode(in, to));
    }

    public void registerDecoder(Decoder<?> decoder) {
        if (decoders.containsKey(decoder.getType().getCode())) {
            throw new IllegalArgumentException("Duplicate message type: " + decoder.getType().getCode());
        }
        decoders.put(decoder.getType().getCode(), decoder);
    }
}
