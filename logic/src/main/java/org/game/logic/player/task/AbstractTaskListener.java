package org.game.logic.player.task;

import org.game.common.event.Listener;
import org.game.common.event.Publisher;
import org.game.config.entity.CfgTask;
import org.game.logic.player.PlayerEventType;

/**
 * @author ：王叶峰
 * @description：任务监听器
 */
public abstract class AbstractTaskListener<Event, T extends Task, K extends CfgTask> implements Listener<Event> {

    protected T task;

    protected K cfg;

    protected TaskStrategy taskStrategy;

    public AbstractTaskListener(T task, K cfg, PlayerEventType type) {
        this.task = task;
        this.cfg = cfg;
        this.taskStrategy = CommonTaskType.getTaskStrategy(type);
    }

    @Override
    public void update(Event event, Publisher<Event> listeners) {
        long target = cfg.getTarget();
        long oldProgress = task.getProgress();
        long newProgress = Math.min(taskStrategy.calculateProgress(event, new TaskData(task.getProgress(), target, cfg.getArgs())), target);
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
