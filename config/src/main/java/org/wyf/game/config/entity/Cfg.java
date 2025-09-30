package org.wyf.game.config.entity;

/**
 * 配置表接口
 *
 * @param <ID> ID类型
 */
public interface Cfg<ID> {

    /**
     * 返回配置对象的ID。
     *
     * @return ID
     */
    ID getId();

    /**
     * 验证配置对象是否合法。
     */
    default void validate() throws Exception {
    }
}
