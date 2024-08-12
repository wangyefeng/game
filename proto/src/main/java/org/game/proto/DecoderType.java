package org.game.proto;

import org.game.proto.protocol.Protocol;

/**
 * 消息解码器类型枚举
 *
 * @author wangyefeng
 * @date 2024-07-13
 * @see Topic
 * @see Protocol
 */
public enum DecoderType {

    /**
     * 消息类型：玩家ID消息（gate和logic之间的消息）
     * +--------+------------+------------+-------------+--------------+------------+
     * | Length | DecoderType|  Topic     | player ID   | Protocol Code|  protobuf  |
     * +--------+------------+------------+-------------+--------------+------------+
     * | 4 bytes| 1 byte     | 1 byte     | 4 bytes     | 2 bytes      | remaining  |
     * +--------+------------+------------+-------------+--------------+------------+
     */
    MESSAGE_PLAYER((byte) 0),

    /**
     * 消息类型：只有协议号的消息(如client和gate之间的消息，logic和gate之间的消息)
     * +--------+------------+------------+---------------+------------+
     * | Length | DecoderType|  Topic     | Protocol Code |  protobuf  |
     * +--------+------------+------------+---------------+------------+
     * | 4 bytes| 1 byte     | 1 byte     | 2 bytes       | remaining  |
     * +--------+------------+------------+---------------+------------+
     */
    MESSAGE_CODE((byte) 1),
    ;

    private final byte code;

    DecoderType(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }
}
