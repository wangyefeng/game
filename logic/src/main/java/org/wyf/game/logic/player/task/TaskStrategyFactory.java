package org.wyf.game.logic.player.task;

import org.wyf.game.config.entity.PlayerEvent;

import java.util.EnumMap;
import java.util.Map;

public abstract class TaskStrategyFactory {

    private static final Map<PlayerEvent, TaskStrategy<?>> taskStrategyMap = new EnumMap<>(PlayerEvent.class);

    static {
        taskStrategyMap.put(PlayerEvent.LEVEL_UP, new LevelTaskStrategy());
    }

    public static TaskStrategy<?> getTaskStrategy(PlayerEvent type) {
        TaskStrategy<?> strategy = taskStrategyMap.get(type);

        if (strategy == null) {
            throw new IllegalArgumentException("No task strategy found for event type: " + type);
        }

        return strategy;
    }

}
