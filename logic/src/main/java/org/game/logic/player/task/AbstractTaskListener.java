package org.game.logic.player.task;

import org.game.common.event.Listener;
import org.game.common.event.Publisher;
import org.game.config.entity.CfgTask;
import org.game.logic.entity.DBTask;
import org.game.logic.player.PlayerEventType;

/**
 * @author ：王叶峰
 * @date ：2023-03-14
 * @description：任务监听器
 */
public abstract class AbstractTaskListener<Event, T extends DBTask, K extends CfgTask> implements Listener<Event> {

    protected T task;

    protected K cfgTask;

    protected TaskStrategy taskStrategy;

    public AbstractTaskListener(T task, K cfgTask, PlayerEventType type) {
        this.task = task;
        this.cfgTask = cfgTask;
        this.taskStrategy = CommonTaskType.getTaskStrategy(type);
    }

    @Override
    public void update(Event event, Publisher<Event> listeners) {
        long target = cfgTask.getTarget();
        long oldProgress = task.getProgress();
        long newProgress = Math.min(taskStrategy.calculateProgress(event, new TaskData(task.getProgress(), target, cfgTask.getArgs())), target);
        if (newProgress != oldProgress) {
            task.setProgress(newProgress);
            if (task.getProgress() >= target) {
                task.setFinished(true);
            }
            update0(event);
        }
        if (task.isFinished()) {
            listeners.unload(this);
        }
    }

    protected abstract void update0(Event event);
}
