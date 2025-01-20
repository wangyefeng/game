package org.game.logic.player.function;

import org.game.config.Configs;
import org.game.config.entity.CfgCyclicFunction;
import org.game.config.entity.CfgFunction;
import org.game.config.entity.ModuleEnum;
import org.game.config.service.CfgCyclicFunctionService;
import org.game.logic.AbstractGameService;
import org.game.logic.entity.DbCycleFunction;
import org.game.logic.entity.FunctionInfo;
import org.game.logic.player.DailyReset;
import org.game.logic.player.PlayerService;
import org.game.logic.player.task.TaskService;
import org.game.logic.repository.FunctionRepository;
import org.game.proto.struct.Login.PbLoginResp.Builder;
import org.game.proto.struct.Login.PbRegisterReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FunctionService extends AbstractGameService<FunctionInfo, FunctionRepository> implements DailyReset {

    private static final Logger log = LoggerFactory.getLogger(FunctionService.class);

    private Map<ModuleEnum, Module> extendModuleMap = new HashMap<>();

    @Override
    public void register(PbRegisterReq registerMsg) {
        entity = new FunctionInfo(player.getId());
    }

    @Override
    public void afterInit() {
        super.afterInit();
        checkCyclicFunction(false);
    }

    /**
     * 检查周期性功能是否需要开启
     *
     * @param isSend 是否需要推送客户端
     */
    public void checkCyclicFunction(boolean isSend) {
        Configs configs = Configs.getInstance();
        CfgCyclicFunctionService cfgCyclicFunctionService = configs.get(CfgCyclicFunctionService.class);
        for (CfgCyclicFunction cfgCyclicFunction : cfgCyclicFunctionService.getAllCfg()) {
            checkCyclicFunctionOne(cfgCyclicFunction, isSend);
        }
    }

    private void checkCyclicFunctionOne(CfgCyclicFunction cfgCyclicFunction, boolean isSend) {
        PlayerService playerService = player.getService(PlayerService.class);
        LocalDate dailyResetDate = playerService.getEntity().getDailyResetDate();
        Map<Integer, DbCycleFunction> cycleFunctions = entity.getCycleFunctions();
        if (!cycleFunctions.containsKey(cfgCyclicFunction.getId())) {
            open(cfgCyclicFunction, isSend);
            cycleFunctions.put(cfgCyclicFunction.getId(), new DbCycleFunction(cfgCyclicFunction.getId(), dailyResetDate));
        } else {
            DbCycleFunction dbCycleFunction = cycleFunctions.get(cfgCyclicFunction.getId());
            LocalDate baseDate = cfgCyclicFunction.getBaseDate();
            int cycle = cfgCyclicFunction.getCycle();
            // 判断是否在同一周期
            if (getCycle(dailyResetDate, baseDate, cycle) != getCycle(dbCycleFunction.getResetDate(), baseDate, cycle)) {
                close(cfgCyclicFunction, isSend);
                open(cfgCyclicFunction, isSend);
                dbCycleFunction.setResetDate(dailyResetDate);
            }
        }
    }

    private static long getCycle(LocalDate date, LocalDate baseDate, int cycle) {
        return ChronoUnit.DAYS.between(date, baseDate) / cycle;
    }

    @Override
    public void loginResp(Builder loginResp) {
        // do nothing
    }

    public void registerModule(Module module) {
        extendModuleMap.put(module.getModuleEnum(), module);
    }

    public void open(CfgFunction cfgFunction, boolean isSend) {
        log.info("玩家{}打开功能{}", player.getId(), cfgFunction.getId());
        entity.addFunctionId(cfgFunction.getId());
        ModuleEnum[] modules = cfgFunction.getModuleEnums();
        for (ModuleEnum moduleEnum : modules) {
            Module module = extendModuleMap.get(moduleEnum);
            try {
                module.open(cfgFunction, isSend);
            } catch (Exception e) {
                log.error("玩家{}打开功能{}失败", player.getId(), cfgFunction.getId(), e);
            }
        }
    }

    public void close(CfgFunction cfgFunction, boolean isSend) {
        log.info("玩家{}关闭功能{}", player.getId(), cfgFunction.getId());
        int functionId = cfgFunction.getId();
        entity.getFunctionIds().remove(functionId);
        ModuleEnum[] modules = cfgFunction.getModuleEnums();
        for (ModuleEnum moduleEnum : modules) {
            Module module = extendModuleMap.get(moduleEnum);
            if (module != null) {
                try {
                    module.close(cfgFunction, isSend);
                } catch (Exception e) {
                    log.error("玩家{}关闭功能{}失败", player.getId(), functionId, e);
                }
            }
        }
    }

    @Override
    public void init() {
        super.init();
        registerModule(player.getService(TaskService.class));
    }

    @Override
    public void reset(LocalDate resetDate, boolean isSend) {
        checkCyclicFunction(true);
    }
}
