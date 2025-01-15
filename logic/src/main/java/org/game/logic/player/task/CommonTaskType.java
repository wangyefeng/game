package org.game.logic.player.task;

import org.game.logic.player.PlayerEventType;

import java.util.HashMap;
import java.util.Map;

public enum CommonTaskType {



    LEVEL(PlayerEventType.LEVEL_UP, new LevelTaskStrategy()),

    ;

    private static Map<PlayerEventType, CommonTaskType> taskStrategyMap = new HashMap<>();

    private final PlayerEventType type;
    private final TaskStrategy<?> taskStrategy;

    CommonTaskType(PlayerEventType type, TaskStrategy<?> taskStrategy) {
        this.type = type;
        this.taskStrategy = taskStrategy;
    }

    static {
        for (CommonTaskType commonTaskType : CommonTaskType.values()) {
            taskStrategyMap.put(commonTaskType.type, commonTaskType);
        }
    }

    public static TaskStrategy getTaskStrategy(PlayerEventType type) {
        CommonTaskType taskType = taskStrategyMap.get(type);
        if (taskType == null) {
            throw new IllegalArgumentException("非法任务类型：" + type);
        }
        return taskStrategyMap.get(type).taskStrategy;
    }

    public static CommonTaskType getCommonTaskType(int type) {
        return taskStrategyMap.get(type);
    }

    public PlayerEventType getType() {
        return type;
    }

    public TaskStrategy<?> getTaskStrategy() {
        return taskStrategy;
    }
}
