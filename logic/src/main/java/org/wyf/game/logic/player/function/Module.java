package org.wyf.game.logic.player.function;

import org.wyf.game.config.entity.CfgFunction;
import org.wyf.game.config.entity.ModuleEnum;

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
