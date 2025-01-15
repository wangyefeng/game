package org.game.logic.player.task;

import org.game.config.Configs;
import org.game.config.entity.CfgFunction;
import org.game.config.entity.CfgTask;
import org.game.config.service.CfgTaskService;
import org.game.logic.AbstractGameService;
import org.game.logic.entity.DBTask;
import org.game.logic.entity.TaskInfo;
import org.game.logic.player.PlayerEventType;
import org.game.logic.player.function.Module;
import org.game.logic.player.function.ModuleEnum;
import org.game.logic.repository.TaskRepository;
import org.game.proto.struct.Login.PbLoginResp.Builder;
import org.game.proto.struct.Login.PbRegisterReq;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskService extends AbstractGameService<TaskInfo, TaskRepository> implements Module {

    private static Map<Integer, PlayerEventType> eventMap = new HashMap<>();

    static {
        eventMap.put(1, PlayerEventType.LEVEL_UP);
    }

    @Override
    public void register(PbRegisterReq registerMsg) {
        entity = new TaskInfo(player.getId());
    }

    @Override
    public void loginResp(Builder loginResp) {
        // do nothing
    }

    @Override
    public void open(CfgFunction cfg, boolean isSend) {
        CfgTaskService cfgTaskService = Configs.getInstance().get(CfgTaskService.class);
        List<CfgTask> cfgTasks = cfgTaskService.getCfgByFuncId(cfg.getId());
        for (CfgTask cfgTask : cfgTasks) {
            PlayerEventType eventType = eventMap.get(cfgTask.getType());
            DBTask task = new DBTask(cfgTask.getId(), cfgTask.getFunctionId());
            TaskStrategy taskStrategy = CommonTaskType.getTaskStrategy(eventType);
            task.setProgress(Math.min(cfgTask.getTarget(), taskStrategy.initProgress(player)));
            if (task.getProgress() >= cfgTask.getTarget()) {
                task.setFinished(true);
            } else {
                player.addEventListener(eventType, new TaskListenerImpl<>(player, task, cfgTask, eventType));
            }
            entity.addTask(task);
        }
    }

    @Override
    public void close(CfgFunction cfg, boolean isSend) {

    }

    @Override
    public ModuleEnum getModuleEnum() {
        return ModuleEnum.TASK;
    }

    @Override
    public void init() {
        super.init();
        CfgTaskService cfgTaskService = Configs.getInstance().get(CfgTaskService.class);
        for (Entry<Integer, DBTask> entry : entity.getTasks().entrySet()) {
            DBTask task = entry.getValue();
            CfgTask cfg = cfgTaskService.getCfg(task.getId());
            if (!task.isFinished()) {
                PlayerEventType eventType = eventMap.get(cfg.getType());
                player.addEventListener(eventType, new TaskListenerImpl<>(player, task, cfg, eventType));
            }
        }
    }
}
