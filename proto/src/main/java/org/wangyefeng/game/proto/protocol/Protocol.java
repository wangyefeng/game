package org.wangyefeng.game.proto.protocol;

import com.google.protobuf.Parser;
import org.wangyefeng.game.proto.Topic;

/**
 * 协议接口
 */
public interface Protocol {


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

    /**
     * 获取protobuf解析器
     *
     * @return 解析器
     */
    Parser<?> parser();
}
