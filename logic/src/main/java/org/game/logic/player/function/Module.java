package org.game.logic.player.function;

import org.game.config.entity.CfgFunction;

public interface Module {

    /**
     * 模块开启
     */
    void open(CfgFunction cfg, boolean isSend);

    /**
     * 模块关闭
     */
    void close(CfgFunction cfg, boolean isSend);

    /**
     * 获取功能模块
     *
     * @return 功能模块
     */
    ModuleEnum getModuleEnum();
}
