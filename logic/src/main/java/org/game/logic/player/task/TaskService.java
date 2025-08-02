package org.game.logic.player.task;

import org.game.config.Configs;
import org.game.config.entity.CfgFunction;
import org.game.config.entity.CfgSimpleTask;
import org.game.config.entity.ModuleEnum;
import org.game.config.entity.PlayerEvent;
import org.game.config.service.CfgSimpleTaskService;
import org.game.logic.database.entity.DbTask;
import org.game.logic.database.entity.TaskInfo;
import org.game.logic.database.repository.DbTaskRepository;
import org.game.logic.database.repository.TaskRepository;
import org.game.logic.player.function.AbstractModuleService;
import org.game.logic.thread.ThreadPool;
import org.game.proto.protocol.LogicToClientProtocol;
import org.game.proto.struct.Common.PbIntArray;
import org.game.proto.struct.Login.PbLoginResp.Builder;
import org.game.proto.struct.Login.PbRegisterReq;
import org.game.proto.struct.Task.PbTaskArrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

@Service
public class TaskService extends AbstractModuleService<TaskInfo, TaskRepository> {

    @Autowired
    private DbTaskRepository dbTaskRepository;

    @Override
    public void load() {
        super.load();
        Collection<DbTask> tasks = dbTaskRepository.findByPlayerId(player.getId());
        entity.init(tasks);
    }

    @Override
    protected void save(TaskInfo entity, boolean cacheEvict) {
        super.save(entity, cacheEvict);
        for (DbTask task : entity.getTasks().values()) {
            dbTaskRepository.save(task, cacheEvict);
        }
    }

    @Override
    public void register(PbRegisterReq registerMsg) {
        entity = new TaskInfo(player.getId());
    }

    @Override
    public void loginResp(Builder loginResp) {
        for (DbTask task : entity.getTasks().values()) {
            loginResp.addTasks(TaskUtil.toPbTask(task));
        }
    }

    @Override
    public void open(CfgFunction cfg, boolean isSend) {
        CfgSimpleTaskService cfgSimpleTaskService = Configs.of(CfgSimpleTaskService.class);
        List<CfgSimpleTask> cfgTasks = cfgSimpleTaskService.getCfgByFuncId(cfg.getId());
        PbTaskArrays.Builder pbTasks = null;
        if (isSend) {
            pbTasks = PbTaskArrays.newBuilder();
        }
        for (CfgSimpleTask cfgTask : cfgTasks) {
            PlayerEvent eventType = cfgTask.getEvent();
            TaskStrategy<?> taskStrategy = TaskStrategyFactory.getTaskStrategy(eventType);
            DbTask task = new DbTask(player.getId(), cfgTask.getId(), Math.min(cfgTask.getTarget(), taskStrategy.initProgress(player)));
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
        CfgSimpleTaskService cfgSimpleTaskService = Configs.of(CfgSimpleTaskService.class);
        // 功能关闭时，清除此功能对应的所有任务
        PbIntArray.Builder pbTasks = null;
        if (isSend) {
            pbTasks = PbIntArray.newBuilder();
        }
        Iterator<DbTask> iterator = entity.getTasks().values().iterator();
        while (iterator.hasNext()) {
            DbTask task = iterator.next();
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
                player.execute(() -> dbTaskRepository.delete(task));
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
        CfgSimpleTaskService cfgSimpleTaskService = Configs.of(CfgSimpleTaskService.class);
        for (Entry<Integer, DbTask> entry : entity.getTasks().entrySet()) {
            DbTask task = entry.getValue();
            CfgSimpleTask cfg = cfgSimpleTaskService.getCfg(task.getId());
            if (cfg.getTarget() <= task.getProgress()) {
                task.setFinished(true);
                task.setProgress(cfg.getTarget());
            }
            if (!task.isFinished()) {
                TaskListenerImpl<Object> listener = new TaskListenerImpl<>(player, task, cfg);
                player.addListener(cfg.getEvent(), listener);
                task.setListener(listener);
            }
        }
    }
}
