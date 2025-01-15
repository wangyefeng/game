package org.game.logic.player.task;

import org.game.config.entity.CfgTask;
import org.game.logic.entity.DBTask;
import org.game.logic.player.Player;
import org.game.logic.player.PlayerEventType;
import org.game.proto.protocol.LogicToClientProtocol;
import org.game.proto.struct.Task.PbTask;
import org.game.proto.struct.Task.PbTask.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskListenerImpl<Event> extends AbstractTaskListener<Event, DBTask, CfgTask> {
    private static final Logger log = LoggerFactory.getLogger(TaskListenerImpl.class);

    private Player player;

    public TaskListenerImpl(Player player, DBTask task, CfgTask cfgTask, PlayerEventType type) {
        super(task, cfgTask, type);
        this.player = player;
    }

    @Override
    protected void update0(Event event) {
        Builder pbTaskBuilder = PbTask.newBuilder();
        pbTaskBuilder.setId(task.getId());
        pbTaskBuilder.setProgress(task.getProgress());
        pbTaskBuilder.setIsFinished(task.isFinished());
        pbTaskBuilder.setIsReward(task.isReward());
        player.writeToClient(LogicToClientProtocol.TASK_UPDATE, pbTaskBuilder.build());
        log.info("玩家{}任务进度更新 {}", player.getId(), pbTaskBuilder);
    }
}
