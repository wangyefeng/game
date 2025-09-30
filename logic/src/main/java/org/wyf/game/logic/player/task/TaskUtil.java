package org.wyf.game.logic.player.task;

import org.wyf.game.logic.database.entity.DbTask;
import org.wyf.game.proto.struct.Task.PbTask;

public abstract class TaskUtil {

    public static PbTask toPbTask(DbTask task) {
        PbTask.Builder builder = PbTask.newBuilder();
        builder.setId(task.getId());
        builder.setProgress(task.getProgress());
        builder.setIsFinished(task.isFinished());
        builder.setIsReward(task.isReward());
        return builder.build();
    }
}
