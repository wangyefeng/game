package org.game.logic.player.task;

import org.game.config.Configs;
import org.game.config.entity.CfgFunction;
import org.game.config.entity.CfgSimpleTask;
import org.game.config.entity.ModuleEnum;
import org.game.config.entity.PlayerEvent;
import org.game.config.service.CfgSimpleTaskService;
import org.game.logic.AbstractGameService;
import org.game.logic.entity.DBTask;
import org.game.logic.entity.TaskInfo;
import org.game.logic.player.function.Module;
import org.game.logic.repository.TaskRepository;
import org.game.proto.protocol.LogicToClientProtocol;
import org.game.proto.struct.Common.PbIntArray;
import org.game.proto.struct.Login.PbLoginResp.Builder;
import org.game.proto.struct.Login.PbRegisterReq;
import org.game.proto.struct.Task.PbTaskArrays;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TaskService extends AbstractGameService<TaskInfo, TaskRepository> implements Module {

    @Override
    public void register(PbRegisterReq registerMsg) {
        entity = new TaskInfo(player.getId());
    }

    @Override
    public void loginResp(Builder loginResp) {
        for (DBTask task : entity.getTasks().values()) {
            loginResp.addTasks(TaskUtil.toPbTask(task));
        }
    }

    @Override
    public void open(CfgFunction cfg, boolean isSend) {
        CfgSimpleTaskService cfgSimpleTaskService = Configs.getInstance().get(CfgSimpleTaskService.class);
        List<CfgSimpleTask> cfgTasks = cfgSimpleTaskService.getCfgByFuncId(cfg.getId());
        PbTaskArrays.Builder pbTasks = null;
        if (isSend) {
            pbTasks = PbTaskArrays.newBuilder();
        }
        for (CfgSimpleTask cfgTask : cfgTasks) {
            PlayerEvent eventType = cfgTask.getEvent();
            TaskStrategy<?> taskStrategy = TaskStrategyFactory.getTaskStrategy(eventType);
            DBTask task = new DBTask(cfgTask.getId(), Math.min(cfgTask.getTarget(), taskStrategy.initProgress(player)));
            if (task.getProgress() >= cfgTask.getTarget()) {
                task.setFinished(true);
            } else {
                TaskListenerImpl<?> listener = new TaskListenerImpl<>(player, task, cfgTask);
                player.addListener(eventType, listener);
                task.setListener(listener);
            }
            entity.addTask(task);
            if (isSend) {
                pbTasks.addTasks(TaskUtil.toPbTask(task));
            }
        }
        if (isSend) {
            player.writeToClient(LogicToClientProtocol.TASK_ADD, pbTasks.build());
        }
    }

    @Override
    public void close(CfgFunction cfg, boolean isSend) {
        Configs configs = Configs.getInstance();
        CfgSimpleTaskService cfgSimpleTaskService = configs.get(CfgSimpleTaskService.class);
        // 功能关闭时，清除此功能对应的所有任务
        PbIntArray.Builder pbTasks = null;
        if (isSend) {
            pbTasks = PbIntArray.newBuilder();
        }
        Iterator<DBTask> iterator = entity.getTasks().values().iterator();
        while (iterator.hasNext()) {
            DBTask task = iterator.next();
            CfgSimpleTask cfgTask = cfgSimpleTaskService.getCfg(task.getId());
            if (cfgTask.getFunctionId() == cfg.getId()) {
                PlayerEvent eventType = cfgTask.getEvent();
                iterator.remove();
                if (task.getListener() != null) {
                    player.unloadListener(eventType, task.getListener());
                }
                if (isSend) {
                    pbTasks.addVal(task.getId());
                }
            }
        }
        if (isSend) {
            player.writeToClient(LogicToClientProtocol.TASK_REMOVE, pbTasks.build());
        }
    }

    @Override
    public ModuleEnum getModuleEnum() {
        return ModuleEnum.TASK;
    }

    @Override
    public void init() {
        super.init();
        Configs configs = Configs.getInstance();
        CfgSimpleTaskService cfgSimpleTaskService = configs.get(CfgSimpleTaskService.class);
        for (Entry<Integer, DBTask> entry : entity.getTasks().entrySet()) {
            DBTask task = entry.getValue();
            CfgSimpleTask cfg = cfgSimpleTaskService.getCfg(task.getId());
            if (!task.isFinished()) {
                TaskListenerImpl<Object> listener = new TaskListenerImpl<>(player, task, cfg);
                player.addListener(cfg.getEvent(), listener);
                task.setListener(listener);
            }
        }
    }
}
