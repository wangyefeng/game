package org.wyf.game.logic.database.entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import org.wyf.game.logic.player.task.Task;
import org.wyf.game.logic.player.task.TaskListenerImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 任务数据
 */
@Entity
@IdClass(DbTask.PK.class)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DbTask implements Cloneable, Task {

    @Id
    private int playerId;

    /**
     * 任务ID
     */
    @Id
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

    public DbTask() {
    }

    public DbTask(int playerId, int id, long progress) {
        this.id = id;
        this.progress = progress;
        this.isFinished = false;
        this.isReward = false;
        this.playerId = playerId;
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
    public DbTask clone() throws CloneNotSupportedException {
        return (DbTask) super.clone();
    }

    public record PK(int playerId, int id) {
    }
}
