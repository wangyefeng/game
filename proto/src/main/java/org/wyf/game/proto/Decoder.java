package org.wyf.game.proto;

import io.netty.buffer.ByteBuf;

/**
 * Decode interface
 * @author wyf
 */
public interface Decoder {

    /**
     * decode
     * @param msg
     */
    Object decode(ByteBuf msg, byte to) throws Exception;

    /**
     * type
     */
    DecoderType getType();

}
