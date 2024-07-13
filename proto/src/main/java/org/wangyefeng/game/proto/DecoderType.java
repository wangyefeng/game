package org.wangyefeng.game.proto;

public enum DecoderType {

    /**
     * 消息类型：玩家消息
     * [协议长度]4+[协议类型]1+[协议号]1+[playerId]4+[protobuf数据]协议长度-6
     * [4][1][1][2][4][protobuf数据]
     */
    MESSAGE_PLAYER((byte) 0),

    /**
     * 消息类型：协议号消息
     * [协议长度]4+[类型]1+[协议号]1+[protobuf数据]协议长度-2
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
