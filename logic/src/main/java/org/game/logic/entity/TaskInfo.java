package org.game.logic.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 任务信息
 */
@Document
public class TaskInfo extends Entity {

    /**
     * 任务列表
     */
    private Map<Integer, DBTask> tasks;

    public TaskInfo(int playerId) {
        super(playerId);
        this.tasks = new HashMap<>();
    }

    public void addTask(DBTask task) {
        tasks.put(task.getId(), task);
    }

    public DBTask getTask(int taskId) {
        return tasks.get(taskId);
    }

    @Override
    public TaskInfo clone() throws CloneNotSupportedException {
        TaskInfo copy = (TaskInfo) super.clone();
        copy.tasks = new HashMap<>();
        for (Entry<Integer, DBTask> entry : tasks.entrySet()) {
            copy.tasks.put(entry.getKey(), entry.getValue().clone());
        }
        return copy;
    }

    public Map<Integer, DBTask> getTasks() {
        return tasks;
    }
}
