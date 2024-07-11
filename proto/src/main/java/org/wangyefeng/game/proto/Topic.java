package org.wangyefeng.game.proto;

/**
 * 协议主体枚举
 *
 * @author wangyefeng
 * @date 2024-07-11
 */
public enum Topic {

    GATE((byte) 1),

    LOGIC((byte) 2),

    CLIENT((byte) 3),

    ;

    private final byte code;

    Topic(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }
}