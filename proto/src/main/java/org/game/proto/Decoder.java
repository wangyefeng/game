package org.game.proto;

import io.netty.buffer.ByteBuf;

/**
 * Decode interface
 * @author wangyefeng
 * @param <T>
 */
public interface Decoder<T> {

    /**
     * decode
     * @param msg
     */
    T decode(ByteBuf msg, byte to) throws Exception;

    /**
     * type
     */
    DecoderType getType();

}
