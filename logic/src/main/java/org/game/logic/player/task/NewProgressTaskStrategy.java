package org.game.logic.player.task;

public class NewProgressTaskStrategy<T extends Number> extends AbstractTaskStrategy<T> {

    @Override
    public long calculateProgress(T t, TaskData taskData) {
        return t.longValue();
    }
}
