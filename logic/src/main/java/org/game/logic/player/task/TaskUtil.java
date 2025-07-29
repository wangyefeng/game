package org.game.logic.player.task;

import org.game.logic.entity.DBTask;
import org.game.proto.struct.Task.PbTask;

public abstract class TaskUtil {

    public static PbTask toPbTask(DBTask task) {
        PbTask.Builder builder = PbTask.newBuilder();
        builder.setId(task.getId());
        builder.setProgress(task.getProgress());
        builder.setIsFinished(task.isFinished());
        builder.setIsReward(task.isReward());
        return builder.build();
    }
}
