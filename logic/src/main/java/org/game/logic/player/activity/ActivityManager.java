package org.game.logic.player.activity;

import org.game.config.Config;
import org.game.config.Configs;
import org.game.config.entity.CfgActivity;
import org.game.config.service.CfgActivityService;
import org.game.logic.player.Players;
import org.game.logic.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 活动管理器
 *
 * @author 王叶峰
 * @date 2022年3月18日
 */
@Component
public class ActivityManager {

    private static final Logger log = LoggerFactory.getLogger(ActivityManager.class);

    private Set<Integer> activityIds = new HashSet<>();

    // 活动全局锁
    public ReadWriteLock lock = new ReentrantReadWriteLock();

    @Autowired
    private Config config;

    public void init() {
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            LocalDateTime nowDateTime = LocalDateTime.now();
            Configs cfg = Configs.getInstance();
            CfgActivityService cfgActivityService = cfg.get(CfgActivityService.class);
            cfgActivityService.getAllCfg().forEach(cfgActivity -> {
                LocalDateTime startTime = cfgActivity.getStartTime();
                LocalDateTime endTime = cfgActivity.getEndTime();
                if (startTime.isBefore(nowDateTime) && endTime.isAfter(nowDateTime)) {
                    startActivity(cfgActivity, nowDateTime, endTime);
                } else if (startTime.isAfter(nowDateTime)) {
                    // 未开始的活动，延迟开启
                    Duration duration = Duration.between(nowDateTime, startTime);
                    long millis = duration.toMillis();
                    ThreadPool.getScheduledExecutor().schedule(
                            () -> startActivity(cfgActivity, nowDateTime, endTime),
                            millis,
                            TimeUnit.MILLISECONDS);    // 定时开启活动
                }
            });
            config.addReloadPublisher((_, _) -> {
                log.info("活动配置变更，重新加载活动！");
            });
        } finally {
            writeLock.unlock();
        }
    }

    private void startActivity(CfgActivity cfgActivity, LocalDateTime nowDateTime, LocalDateTime endTime) {
        open(cfgActivity);
        Duration duration = Duration.between(nowDateTime, endTime);
        long millis = duration.toMillis();
        ThreadPool.getScheduledExecutor().schedule(
                () -> close(cfgActivity),
                millis,
                TimeUnit.MILLISECONDS);    // 定时关闭活动
    }

    public boolean isOpen(int id) {
        return activityIds.contains(id);
    }

    public void open(CfgActivity cfg) {
        log.info("活动{}开启！", cfg.getId());
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            activityIds.add(cfg.getId());
        } finally {
            writeLock.unlock();
        }
        Players.getPlayers().values().forEach(player -> {
            ActivityService activityService = player.getService(ActivityService.class);
            activityService.checkActivity(cfg, true);
        });
    }

    public void close(CfgActivity cfg) {
        log.info("活动{}关闭！", cfg.getId());
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            activityIds.remove(cfg.getId());
        } finally {
            writeLock.unlock();
        }
        Players.getPlayers().values().forEach(player -> {
            ActivityService activityService = player.getService(ActivityService.class);
            activityService.checkActivity(cfg, true);
        });
    }

    public Set<Integer> getActivityIds() {
        return activityIds;
    }
}
