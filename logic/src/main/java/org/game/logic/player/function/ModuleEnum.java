package org.game.logic.player.function;

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

    private final int module;

    ModuleEnum(int module) {
        this.module = module;
    }

    public int getModule() {
        return module;
    }
}
