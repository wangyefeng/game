package org.wangyefeng.game.logic.net;

/**
 * 协议体结构类型枚举
 */
public enum ProtocolType {

    /**
     * 客户端到逻辑服务器的协议体类型
     */
    LOGIC_CLIENT(0),

    /**
     * 逻辑服务器到网关服务器的协议体类型
     */
    LOGIC_GATE(1),
    ;
    private int value;


    ProtocolType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
