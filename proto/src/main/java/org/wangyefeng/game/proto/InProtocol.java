package org.wangyefeng.game.proto;

import com.google.protobuf.Parser;

/**
 * 输入协议接口
 *
 * @author wangyefeng
 * @date 2024-07-11
 */
public interface InProtocol extends Protocol {

    /**
     * 获取protobuf解析器
     *
     * @return 解析器
     */
    Parser<?> parser();
}
