package org.game.logic.database.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 任务信息
 */
@jakarta.persistence.Entity
@Table
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TaskInfo extends BaseInfo {

    /**
     * 任务列表
     */
    @Transient
    private Map<Integer, DbTask> tasks = new HashMap<>();

    private TaskInfo() {
        // for JPA
    }

    public TaskInfo(int playerId) {
        super(playerId);
    }

    public void init(Collection<DbTask> tasks) {
        for (DbTask task : tasks) {
            this.tasks.put(task.getId(), task);
        }
    }

    public void addTask(DbTask task) {
        tasks.put(task.getId(), task);
    }

    public DbTask getTask(int taskId) {
        return tasks.get(taskId);
    }

    @Override
    public TaskInfo clone() throws CloneNotSupportedException {
        TaskInfo copy = (TaskInfo) super.clone();
        copy.tasks = new HashMap<>();
        for (Entry<Integer, DbTask> entry : tasks.entrySet()) {
            copy.tasks.put(entry.getKey(), entry.getValue().clone());
        }
        return copy;
    }

    public Map<Integer, DbTask> getTasks() {
        return tasks;
    }
}
