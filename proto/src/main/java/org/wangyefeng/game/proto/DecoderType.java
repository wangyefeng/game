package org.wangyefeng.game.proto;

/**
 * 消息解码器类型枚举
 *
 * @author wangyefeng
 * @date 2024-07-13
 * @see Topic
 * @see org.wangyefeng.game.proto.protocol.Protocol
 */
public enum DecoderType {

    /**
     * 消息类型：玩家ID消息
     * +--------+------------+------------+---------------+------------+------------+
     * | Length | DecoderType| From Topic | Protocol Code | player ID  |  protobuf  |
     * +--------+------------+------------+---------------+------------+------------+
     * | 4 bytes| 1 byte     | 1 byte     | 2 bytes       | 4 bytes    | remaining  |
     * +--------+------------+------------+---------------+------------+------------+
     */
    MESSAGE_PLAYER((byte) 0),

    /**
     * 消息类型：玩家ID消息
     * +--------+------------+------------+---------------+------------+
     * | Length | DecoderType| From Topic | Protocol Code |  protobuf  |
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
