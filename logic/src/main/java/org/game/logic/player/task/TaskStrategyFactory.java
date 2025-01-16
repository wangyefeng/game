package org.game.logic.player.task;

import org.game.logic.player.PlayerEventType;

import java.util.EnumMap;
import java.util.Map;

public abstract class TaskStrategyFactory {

    private static final Map<PlayerEventType, LevelTaskStrategy> taskStrategyMap = new EnumMap<>(PlayerEventType.class);

    static {
        taskStrategyMap.put(PlayerEventType.LEVEL_UP, new LevelTaskStrategy());
    }

    public static TaskStrategy<?> getTaskStrategy(PlayerEventType type) {
        TaskStrategy<?> strategy = taskStrategyMap.get(type);

        if (strategy == null) {
            // 可以抛出自定义异常，或者返回一个默认策略
            throw new IllegalArgumentException("No task strategy found for event type: " + type);
        }

        return strategy;
    }

}
