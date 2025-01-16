package org.game.logic.player.function;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能枚举
 *
 * @author 王叶峰
 */
public abstract class FunctionFactory {

    private static Map<Integer, ModuleEnum[]> functionModules = new HashMap<>();

    static {
        addFunctionModules(1, ModuleEnum.TASK);
        addFunctionModules(2, ModuleEnum.TASK);
        addFunctionModules(3, ModuleEnum.TASK);
    }

    private static void addFunctionModules(int id, ModuleEnum... modules) {
        functionModules.put(id, modules);
    }

    public static ModuleEnum[] getModules(int id) {
        return functionModules.get(id);
    }
}
