package org.game.logic.player.activity;

import org.game.config.Configs;
import org.game.config.entity.CfgTimeIntervalFunction;
import org.game.config.service.CfgTimeIntervalFunctionService;
import org.game.logic.AbstractGameService;
import org.game.logic.entity.TimeIntervalFunctionInfo;
import org.game.logic.player.function.FunctionService;
import org.game.logic.repository.TimeIntervalFunctionRepository;
import org.game.proto.struct.Login.PbLoginResp.Builder;
import org.game.proto.struct.Login.PbRegisterReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;

/**
 * 时间段开启的功能服务
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TimeIntervalFunctionService extends AbstractGameService<TimeIntervalFunctionInfo, TimeIntervalFunctionRepository> {

    @Autowired
    private TimeIntervalManager timeIntervalManager;

    @Override
    public void register(PbRegisterReq registerMsg) {
        entity = new TimeIntervalFunctionInfo(player.getId());
    }

    @Override
    public void init() {
        check(false);
    }

    public void check(boolean isSend) {
        Lock lock = timeIntervalManager.lock.readLock();
        try {
            lock.lock();
            Set<Integer> needCheck = new HashSet<>(timeIntervalManager.getFunctionIds());
            needCheck.addAll(entity.getFunctionIds());
            Configs configs = Configs.getInstance();
            CfgTimeIntervalFunctionService cfgTimeIntervalFunctionService = configs.get(CfgTimeIntervalFunctionService.class);
            for (Integer id : needCheck) {
                CfgTimeIntervalFunction cfg = cfgTimeIntervalFunctionService.getCfg(id);
                checkOne(cfg, isSend);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void loginResp(Builder loginResp) {
        // do nothing
    }

    public void check(CfgTimeIntervalFunction cfg, boolean isSend) {
        Lock lock = timeIntervalManager.lock.readLock();
        try {
            lock.lock();
            checkOne(cfg, isSend);
        } catch (Exception e) {
            throw e;
        } finally {
            lock.unlock();
        }
    }

    private void checkOne(CfgTimeIntervalFunction cfg, boolean isSend) {
        int id = cfg.getId();
        boolean isOpen = timeIntervalManager.isOpen(id);
        boolean playerIsOpen = entity.getFunctionIds().contains(id);
        FunctionService functionService = player.getService(FunctionService.class);
        if (isOpen != playerIsOpen) {// 状态不一致
            if (isOpen) {
                functionService.open(cfg, isSend);
                entity.getFunctionIds().add(id);
            } else {
                functionService.close(cfg, isSend);
                entity.getFunctionIds().remove(id);
            }
        }
    }

}
