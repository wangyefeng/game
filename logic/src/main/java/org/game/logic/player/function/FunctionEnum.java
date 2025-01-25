package org.game.logic.player.function;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能枚举
 *
 * @author 王叶峰
 */
public enum FunctionEnum {

    TASK(1, ModuleEnum.TASK),
    ;

    private static Map<Integer, FunctionEnum> functionType = new HashMap<>();

    static {
        FunctionEnum[] functionEnums = FunctionEnum.values();
        for (FunctionEnum functionEnum : functionEnums) {
            functionType.put(functionEnum.getType(), functionEnum);
        }
    }

    private final int type;

    private final ModuleEnum[] modules;

    FunctionEnum(int type, ModuleEnum... modules) {
        this.type = type;
        this.modules = modules;
    }

    public int getType() {
        return type;
    }

    public ModuleEnum[] getModules() {
        return modules;
    }

    public static ModuleEnum[] getModulesByType(int type) {
        FunctionEnum functionEnum = functionType.get(type);
        if (functionEnum == null) {
            return null;
        }
        return functionEnum.getModules();
    }

    public static FunctionEnum getByType(int type) {
        return functionType.get(type);
    }
}
