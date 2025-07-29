package org.game.logic.entity;

import org.game.logic.player.task.Task;
import org.game.logic.player.task.TaskListenerImpl;
import org.springframework.data.annotation.Transient;

/**
 * 任务数据
 */
public class DBTask implements Cloneable, Task {

    /**
     * 任务ID
     */
    private int id;

    /**
     * 任务进度
     */
    private long progress;

    /**
     * 任务是否完成
     */
    private boolean isFinished;

    /**
     * 任务是否奖励
     */
    private boolean isReward;

    @Transient
    private TaskListenerImpl<?> listener;

    public DBTask() {
    }

    public DBTask(int id) {
        this(id, 0);
    }

    public DBTask(int id, long progress) {
        this.id = id;
        this.progress = progress;
        this.isFinished = false;
        this.isReward = false;
    }

    public int getId() {
        return id;
    }

    public long getProgress() {
        return progress;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public boolean isReward() {
        return isReward;
    }

    public void setProgress(long progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress must be positive");
        }
        this.progress = progress;
    }

    public void addProgress(long progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress must be positive");
        }
        this.progress += progress;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public void setReward(boolean reward) {
        isReward = reward;
    }

    public TaskListenerImpl<?> getListener() {
        return listener;
    }

    public void setListener(TaskListenerImpl<?> listener) {
        this.listener = listener;
    }

    @Override
    public DBTask clone() throws CloneNotSupportedException {
        return (DBTask) super.clone();
    }
}
