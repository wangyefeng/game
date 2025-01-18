package org.game.logic.player.task;

import org.game.config.entity.PlayerEvent;

import java.util.EnumMap;
import java.util.Map;

public abstract class TaskStrategyFactory {

    private static final Map<PlayerEvent, LevelTaskStrategy> taskStrategyMap = new EnumMap<>(PlayerEvent.class);

    static {
        taskStrategyMap.put(PlayerEvent.LEVEL_UP, new LevelTaskStrategy());
    }

    public static TaskStrategy<?> getTaskStrategy(PlayerEvent type) {
        TaskStrategy<?> strategy = taskStrategyMap.get(type);

        if (strategy == null) {
            // 可以抛出自定义异常，或者返回一个默认策略
            throw new IllegalArgumentException("No task strategy found for event type: " + type);
        }

        return strategy;
    }

}
