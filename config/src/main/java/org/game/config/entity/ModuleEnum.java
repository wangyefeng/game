package org.game.config.entity;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 模块枚举
 *
 * @author 王叶峰
 */
public enum ModuleEnum {

    /**
     * 任务模块
     */
    TASK(1),
    ;

    private final int code;

    ModuleEnum(int code) {
        this.code = code;
    }

    @JsonValue
    public int getCode() {
        return code;
    }
}
