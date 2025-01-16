package org.game.logic.player.function;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
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

    private final Set<Integer> functionIds = new HashSet<>();

    // 全局锁读写锁
    public ReadWriteLock lock = new ReentrantReadWriteLock();

    @Autowired
    private Config config;

    // 活动开启定时器
    private Map<Integer, ScheduledFuture> startScheduledFuture = new HashMap<>();

    // 活动结束定时器
    private Map<Integer, ScheduledFuture> closeScheduledFuture = new HashMap<>();

    public void init() {
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            LocalDateTime nowDateTime = LocalDateTime.now();
            Configs configs = Configs.getInstance();
            CfgTimeIntervalFunctionService cfgService = configs.get(CfgTimeIntervalFunctionService.class);
            cfgService.getAllCfg().forEach(cfg -> initActivity(cfg, nowDateTime));
            config.addReloadPublisher((_, _) -> reload());
        } finally {
            writeLock.unlock();
        }
    }

    private void initActivity(CfgTimeIntervalFunction cfg, LocalDateTime nowDateTime) {
        LocalDateTime startTime = cfg.getStartTime();
        LocalDateTime endTime = cfg.getEndTime();
        if (startTime.isBefore(nowDateTime) && endTime.isAfter(nowDateTime)) {
            start(cfg, nowDateTime, endTime);
        } else if (startTime.isAfter(nowDateTime)) {
            // 未开始的功能，延迟开启
            Duration duration = Duration.between(nowDateTime, startTime);
            long millis = duration.toMillis();
            ScheduledFuture<?> schedule = ThreadPool.getScheduledExecutor().schedule(
                    () -> start(cfg, nowDateTime, endTime),
                    millis,
                    TimeUnit.MILLISECONDS);    // 定时开启活动
            startScheduledFuture.put(cfg.getId(), schedule);
        }
    }

    private void reload() {
        log.info("配置变更，重新检查时间段开启的功能状态！");
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            LocalDateTime nowDateTime = LocalDateTime.now();
            Configs configs = Configs.getInstance();
            CfgTimeIntervalFunctionService cfgService = configs.get(CfgTimeIntervalFunctionService.class);
            for (CfgTimeIntervalFunction cfg : cfgService.getAllCfg()) {
                Integer id = cfg.getId();
                ScheduledFuture<?> startFuture = startScheduledFuture.remove(id);
                if (startFuture != null) {
                    startFuture.cancel(true);
                }
                ScheduledFuture<?> closeFuture = closeScheduledFuture.remove(id);
                if (closeFuture != null) {// 正在进行中...
                    closeFuture.cancel(true);
                    if (nowDateTime.isBefore(cfg.getStartTime())) {
                        close(cfg);
                        initActivity(cfg, nowDateTime);
                    } else if (nowDateTime.isBefore(cfg.getEndTime())) {
                        Duration duration = Duration.between(nowDateTime, cfg.getEndTime());
                        long millis = duration.toMillis();
                        ScheduledFuture<?> schedule = ThreadPool.getScheduledExecutor().schedule(
                                () -> close(cfg),
                                millis,
                                TimeUnit.MILLISECONDS);// 定时关闭活动
                        closeScheduledFuture.put(cfg.getId(), schedule);
                    } else {
                        close(cfg);
                    }
                } else {
                    initActivity(cfg, nowDateTime);
                }
            }
        } finally {
            writeLock.unlock();
        }
    }

    private void start(CfgTimeIntervalFunction cfg, LocalDateTime nowDateTime, LocalDateTime endTime) {
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            if (Thread.interrupted()) {
                return;
            }
            startScheduledFuture.remove(cfg.getId());
            open(cfg);
            Duration duration = Duration.between(nowDateTime, endTime);
            long millis = duration.toMillis();
            ScheduledFuture<?> schedule = ThreadPool.getScheduledExecutor().schedule(
                    () -> close(cfg),
                    millis,
                    TimeUnit.MILLISECONDS);// 定时关闭活动
            closeScheduledFuture.put(cfg.getId(), schedule);
        } finally {
            writeLock.unlock();
        }

    }

    public boolean isOpen(int id) {
        return functionIds.contains(id);
    }

    public void open(CfgTimeIntervalFunction cfg) {
        log.info("活动{}开启！", cfg.getId());
        functionIds.add(cfg.getId());
        Players.getPlayers().values().forEach(player -> {
            FunctionService functionService = player.getService(FunctionService.class);
            functionService.checkTimeIntervalOne(cfg, true);
        });
    }

    public void close(CfgTimeIntervalFunction cfg) {
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            if (Thread.interrupted()) {
                return;
            }
            log.info("功能{}关闭！", cfg.getId());
            closeScheduledFuture.remove(cfg.getId());
            functionIds.remove(cfg.getId());
        } finally {
            writeLock.unlock();
        }
        Players.getPlayers().values().forEach(player -> {
            ThreadPool.getPlayerExecutor(player.getId()).execute(() -> {
                FunctionService functionService = player.getService(FunctionService.class);
                functionService.checkTimeIntervalOne(cfg, true);
            });
        });
    }

    public Set<Integer> getFunctionIds() {
        return functionIds;
    }
}
