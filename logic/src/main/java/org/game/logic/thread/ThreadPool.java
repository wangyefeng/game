package org.game.logic.thread;

import org.game.logic.player.Players;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class ThreadPool {

    private static final Logger log = LoggerFactory.getLogger(ThreadPool.class);

    public static final int EXECUTOR_SIZE = Runtime.getRuntime().availableProcessors();

    public static ThreadPoolExecutor[] playerExecutors;

    public static ThreadPoolExecutor[] playerDBExecutors;

    public static ScheduledExecutorService scheduledExecutor;


    public static void start() {
        playerExecutors = new ThreadPoolExecutor[EXECUTOR_SIZE];
        playerDBExecutors = new ThreadPoolExecutor[EXECUTOR_SIZE];
        for (int i = 0; i < EXECUTOR_SIZE; i++) {
            final int k = i;
            playerExecutors[i] = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> new Thread(r, "player-thread-" + k));
            playerDBExecutors[i] = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> new Thread(r, "player-db-thread-" + k));
        }
        scheduledExecutor = new ScheduledThreadPoolExecutor(5);
        scheduledExecutor.scheduleAtFixedRate(() -> {
            log.debug("实时在线玩家数量{}", Players.getPlayers().size());
        }, 10, 10, TimeUnit.SECONDS);
    }

    public static void shutdown() {
        for (ThreadPoolExecutor executor : ThreadPool.playerExecutors) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                log.error("player thread {} shutdown interrupted", e);
            }
        }
        for (ThreadPoolExecutor executor : ThreadPool.playerDBExecutors) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(600, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                log.error("player thread {} shutdown interrupted", e);
            }
        }
        log.info("player thread pool shutdown");
    }

    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long delay, long period, TimeUnit unit) {
        return scheduledExecutor.scheduleAtFixedRate(runnable, delay, period, unit);
    }

    public static ThreadPoolExecutor getPlayerExecutor(int playerId) {
        return playerExecutors[playerId % EXECUTOR_SIZE];
    }

    public static ThreadPoolExecutor getPlayerDBExecutor(int playerId) {
        return playerDBExecutors[playerId % EXECUTOR_SIZE];
    }
}
