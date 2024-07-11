package org.wangyefeng.game.proto;

public enum DecoderType {

    MESSAGE_PLAYER((byte) 0),

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
