package org.game.logic.player.task;

import org.game.config.entity.CfgTask;
import org.game.logic.entity.DBTask;
import org.game.logic.player.Player;
import org.game.proto.protocol.LogicToClientProtocol;
import org.game.proto.struct.Task.PbTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskListenerImpl<Event> extends AbstractTaskListener<Event, DBTask, CfgTask> {

    private static final Logger log = LoggerFactory.getLogger(TaskListenerImpl.class);

    private Player player;

    public TaskListenerImpl(Player player, DBTask task, CfgTask cfgTask) {
        super(task, cfgTask);
        this.player = player;
    }

    @Override
    protected void update0(Event event) {
        if (task.isFinished()) {
            task.setListener(null);
        }
        PbTask pbTask = TaskUtil.toPbTask(task);
        player.writeToClient(LogicToClientProtocol.TASK_UPDATE, pbTask);
        log.info("玩家{}任务进度更新 {}", player.getId(), pbTask);
    }
}
