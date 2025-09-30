package org.wyf.game.proto.protocol;

import org.wyf.game.proto.Topic;

/**
 * 协议接口
 */
public interface Protocol {


    /**
     * 协议头部长度
     */
    int FRAME_LENGTH = 4;

    /**
     * 获取协议的来源
     *
     * @return 来源
     */
    Topic from();

    /**
     * 获取协议的目目标
     *
     * @return 目标
     */
    Topic to();

    /**
     * 获取协议号
     *
     * @return 协议号
     */
    short getCode();

    static String toString(Protocol protocol) {
        return protocol.getClass().getSimpleName() + "." + protocol;
    }
}
