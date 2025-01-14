package org.game.logic.player.activity;

import org.game.config.Config;
import org.game.config.Configs;
import org.game.config.entity.CfgTimeIntervalFunction;
import org.game.config.service.CfgTimeIntervalFunctionService;
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
 * 按时间段开启的功能的管理器
 *
 * @author 王叶峰
 */
@Component
public class TimeIntervalManager {

    private static final Logger log = LoggerFactory.getLogger(TimeIntervalManager.class);

    private Set<Integer> functionIds = new HashSet<>();

    // 全局锁读写锁
    public ReadWriteLock lock = new ReentrantReadWriteLock();

    @Autowired
    private Config config;

    public void init() {
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            LocalDateTime nowDateTime = LocalDateTime.now();
            Configs configs = Configs.getInstance();
            CfgTimeIntervalFunctionService cfgService = configs.get(CfgTimeIntervalFunctionService.class);
            cfgService.getAllCfg().forEach(cfg -> {
                LocalDateTime startTime = cfg.getStartTime();
                LocalDateTime endTime = cfg.getEndTime();
                if (startTime.isBefore(nowDateTime) && endTime.isAfter(nowDateTime)) {
                    startActivity(cfg, nowDateTime, endTime);
                } else if (startTime.isAfter(nowDateTime)) {
                    // 未开始的功能，延迟开启
                    Duration duration = Duration.between(nowDateTime, startTime);
                    long millis = duration.toMillis();
                    ThreadPool.getScheduledExecutor().schedule(
                            () -> startActivity(cfg, nowDateTime, endTime),
                            millis,
                            TimeUnit.MILLISECONDS);    // 定时开启活动
                }
            });
            config.addReloadPublisher((_, _) -> {
                log.info("配置变更，重新加载时间段开启的功能！");
            });
        } finally {
            writeLock.unlock();
        }
    }

    private void startActivity(CfgTimeIntervalFunction cfg, LocalDateTime nowDateTime, LocalDateTime endTime) {
        open(cfg);
        Duration duration = Duration.between(nowDateTime, endTime);
        long millis = duration.toMillis();
        ThreadPool.getScheduledExecutor().schedule(
                () -> close(cfg),
                millis,
                TimeUnit.MILLISECONDS);    // 定时关闭活动
    }

    public boolean isOpen(int id) {
        return functionIds.contains(id);
    }

    public void open(CfgTimeIntervalFunction cfg) {
        log.info("活动{}开启！", cfg.getId());
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            functionIds.add(cfg.getId());
        } finally {
            writeLock.unlock();
        }
        Players.getPlayers().values().forEach(player -> {
            TimeIntervalFunctionService timeIntervalFunctionService = player.getService(TimeIntervalFunctionService.class);
            timeIntervalFunctionService.check(cfg, true);
        });
    }

    public void close(CfgTimeIntervalFunction cfg) {
        log.info("功能{}关闭！", cfg.getId());
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            functionIds.remove(cfg.getId());
        } finally {
            writeLock.unlock();
        }
        Players.getPlayers().values().forEach(player -> {
            TimeIntervalFunctionService timeIntervalFunctionService = player.getService(TimeIntervalFunctionService.class);
            timeIntervalFunctionService.check(cfg, true);
        });
    }

    public Set<Integer> getFunctionIds() {
        return functionIds;
    }
}
