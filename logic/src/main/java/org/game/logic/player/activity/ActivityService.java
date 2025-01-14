package org.game.logic.player.activity;

import org.game.config.Configs;
import org.game.config.entity.CfgActivity;
import org.game.config.service.CfgActivityService;
import org.game.logic.AbstractGameService;
import org.game.logic.entity.ActivityInfo;
import org.game.logic.player.function.FunctionService;
import org.game.logic.repository.ActivityRepository;
import org.game.proto.struct.Login.PbLoginResp.Builder;
import org.game.proto.struct.Login.PbRegisterReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ActivityService extends AbstractGameService<ActivityInfo, ActivityRepository> {

    @Autowired
    private ActivityManager activityManager;

    @Override
    public void register(PbRegisterReq registerMsg) {
        entity = new ActivityInfo(player.getId());
    }

    @Override
    public void init() {
        checkActivity(false);
    }

    public void checkActivity(boolean isSend) {
        Lock lock = activityManager.lock.readLock();
        try {
            lock.lock();
            Set<Integer> needCheck = new HashSet<>(activityManager.getActivityIds());
            needCheck.addAll(entity.getActivityIds());
            Configs configs = Configs.getInstance();
            CfgActivityService cfgActivityService = configs.get(CfgActivityService.class);
            for (Integer activityId : needCheck) {
                CfgActivity cfgActivity = cfgActivityService.getCfg(activityId);
                checkActivityOne(cfgActivity, isSend);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void loginResp(Builder loginResp) {
        // do nothing
    }

    public void checkActivity(CfgActivity cfgActivity, boolean isSend) {
        Lock lock = activityManager.lock.readLock();
        try {
            lock.lock();
            checkActivityOne(cfgActivity, isSend);
        } catch (Exception e) {
            throw e;
        } finally {
            lock.unlock();
        }
    }

    private void checkActivityOne(CfgActivity cfgActivity, boolean isSend) {
        int id = cfgActivity.getId();
        boolean isOpen = activityManager.isOpen(id);
        boolean playerIsOpen = entity.getActivityIds().contains(id);
        FunctionService functionService = player.getService(FunctionService.class);
        if (isOpen != playerIsOpen) {// 状态不一致
            if (isOpen) {
                functionService.open(cfgActivity, isSend);
                entity.getActivityIds().add(id);
            } else {
                functionService.close(cfgActivity, isSend);
                entity.getActivityIds().remove(id);
            }
        }
    }

}
