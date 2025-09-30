package org.wyf.game.logic.player.task;

import org.wyf.game.config.entity.CfgTask;
import org.wyf.game.logic.database.entity.DbTask;
import org.wyf.game.logic.player.Player;
import org.wyf.game.proto.protocol.LogicToClientProtocol;
import org.wyf.game.proto.struct.Task.PbTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskListenerImpl<Event> extends AbstractTaskListener<Event, DbTask, CfgTask> {

    private static final Logger log = LoggerFactory.getLogger(TaskListenerImpl.class);

    private Player player;

    public TaskListenerImpl(Player player, DbTask task, CfgTask cfgTask) {
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
