package org.wyf.game.logic.player.function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wyf.game.config.Configs;
import org.wyf.game.config.entity.CfgCyclicFunction;
import org.wyf.game.config.entity.CfgFunction;
import org.wyf.game.config.entity.CfgTimeIntervalFunction;
import org.wyf.game.config.entity.ModuleEnum;
import org.wyf.game.config.service.CfgCyclicFunctionService;
import org.wyf.game.config.service.CfgTimeIntervalFunctionService;
import org.wyf.game.logic.database.entity.CycleFunction;
import org.wyf.game.logic.database.entity.FunctionInfo;
import org.wyf.game.logic.database.repository.CycleFunctionRepository;
import org.wyf.game.logic.database.repository.FunctionRepository;
import org.wyf.game.logic.player.AbstractGameService;
import org.wyf.game.logic.player.DailyReset;
import org.wyf.game.logic.player.PlayerService;
import org.wyf.game.proto.struct.Login;
import org.wyf.game.proto.struct.Login.PbRegisterReq;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;

@Service
public class FunctionService extends AbstractGameService<FunctionInfo, FunctionRepository> implements DailyReset {

    private static final Logger log = LoggerFactory.getLogger(FunctionService.class);

    private final Map<ModuleEnum, Module> extendModuleMap = new HashMap<>();

    @Autowired
    private CycleFunctionRepository cycleFunctionRepository;

    @Autowired
    private TimeIntervalManager timeIntervalManager;

    @Override
    public void load() {
        super.load();
        entity.init(cycleFunctionRepository.findByPlayerId(player.getId()));
    }

    @Override
    protected void save(FunctionInfo entity, boolean cacheEvict) {
        super.save(entity, cacheEvict);
        for (CycleFunction cycleFunction : entity.getCycleFunctions().values()) {
            cycleFunctionRepository.save(cycleFunction, cacheEvict);
        }
    }

    @Override
    public void register(PbRegisterReq registerMsg) {
        entity = new FunctionInfo(player.getId());
    }

    @Override
    public void afterInit() {
        super.afterInit();
        checkTimeInterval(false);
        checkCyclicFunction(false);
    }

    /**
     * 检查周期性功能是否需要开启
     *
     * @param isSend 是否需要推送客户端
     */
    public void checkCyclicFunction(boolean isSend) {
        CfgCyclicFunctionService cfgCyclicFunctionService = Configs.of(CfgCyclicFunctionService.class);
        for (CfgCyclicFunction cfgCyclicFunction : cfgCyclicFunctionService.getAllCfg()) {
            checkCyclicFunctionOne(cfgCyclicFunction, isSend);
        }
    }

    private void checkCyclicFunctionOne(CfgCyclicFunction cfgCyclicFunction, boolean isSend) {
        PlayerService playerService = player.getService(PlayerService.class);
        LocalDate dailyResetDate = playerService.getEntity().getDailyResetDate();
        LocalDate startDate = cfgCyclicFunction.getStartDate();
        long betweenDays = ChronoUnit.DAYS.between(startDate, dailyResetDate);
        boolean isOpen;
        if (betweenDays < 0) {
            isOpen = false;
        } else {
            isOpen = betweenDays % cfgCyclicFunction.getCycle() < cfgCyclicFunction.getOpenDays();
        }

        Map<Integer, CycleFunction> cycleFunctions = entity.getCycleFunctions();
        if (!cycleFunctions.containsKey(cfgCyclicFunction.getId())) {
            if (isOpen) {
                open(cfgCyclicFunction, isSend);
                cycleFunctions.put(cfgCyclicFunction.getId(), new CycleFunction(player.getId(), cfgCyclicFunction.getId(), dailyResetDate));
            }
        } else {
            CycleFunction cycleFunction = cycleFunctions.get(cfgCyclicFunction.getId());
            int cycle = cfgCyclicFunction.getCycle();
            // 判断是否在同一周期
            if (getCycle(dailyResetDate, startDate, cycle) != getCycle(cycleFunction.getResetDate(), startDate, cycle)) {// 不在同一周期
                close(cfgCyclicFunction, isSend);
                if (isOpen) {
                    open(cfgCyclicFunction, isSend);
                    cycleFunction.setResetDate(dailyResetDate);
                } else {
                    cycleFunctions.remove(cfgCyclicFunction.getId());
                    player.dbExecute(() -> cycleFunctionRepository.delete(cycleFunction));
                }
            } else {// 在同一周期
                if (!isOpen) {// 关闭
                    close(cfgCyclicFunction, isSend);
                    cycleFunctions.remove(cfgCyclicFunction.getId());
                    player.dbExecute(() -> cycleFunctionRepository.delete(cycleFunction));
                }
            }
        }
    }

    private static long getCycle(LocalDate date, LocalDate startDate, int cycle) {
        return ChronoUnit.DAYS.between(startDate, date) / cycle;
    }

    @Override
    public void loginResp(Login.PbLoginOrRegisterResp.Builder loginResp) {
        // do nothing
    }

    public void registerModule(Module module) {
        extendModuleMap.put(module.getModuleEnum(), module);
    }

    public void open(CfgFunction cfgFunction, boolean isSend) {
        log.info("玩家{}打开功能{}", player.getId(), cfgFunction.getId());
        entity.addFunctionId(cfgFunction.getId());
        ModuleEnum[] modules = cfgFunction.getModules();
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
        ModuleEnum[] modules = cfgFunction.getModules();
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
    public void reset(LocalDate resetDate, boolean isSend) {
        checkCyclicFunction(true);
    }

    public void checkTimeInterval(boolean isSend) {
        Lock lock = timeIntervalManager.lock.readLock();
        try {
            lock.lock();
            // 交集
            Set<Integer> intersection = new HashSet<>(timeIntervalManager.getFunctionIds());
            intersection.retainAll(entity.getTimeIntervalIds());

            // 需要检查的功能 取并集后去掉交集
            Set<Integer> needCheck = new HashSet<>(timeIntervalManager.getFunctionIds());
            needCheck.addAll(entity.getTimeIntervalIds());
            needCheck.removeAll(intersection);

            CfgTimeIntervalFunctionService cfgTimeIntervalFunctionService = Configs.of(CfgTimeIntervalFunctionService.class);
            for (Integer id : needCheck) {
                CfgTimeIntervalFunction cfg = cfgTimeIntervalFunctionService.getCfg(id);
                checkTimeIntervalOne(cfg, isSend);
            }
        } finally {
            lock.unlock();
        }
    }

    public void checkTimeIntervalOne(CfgTimeIntervalFunction cfg, boolean isSend) {
        Lock writeLock = timeIntervalManager.lock.readLock();
        try {
            writeLock.lock();
            int id = cfg.getId();
            boolean isOpen = timeIntervalManager.isOpen(id);
            boolean playerIsOpen = entity.getFunctionIds().contains(id);
            FunctionService functionService = player.getService(FunctionService.class);
            if (isOpen != playerIsOpen) {// 状态不一致
                if (isOpen) {
                    functionService.open(cfg, isSend);
                    entity.getTimeIntervalIds().add(id);
                } else {
                    functionService.close(cfg, isSend);
                    entity.getTimeIntervalIds().remove(id);
                }
            }
        } finally {
            writeLock.unlock();
        }
    }
}
