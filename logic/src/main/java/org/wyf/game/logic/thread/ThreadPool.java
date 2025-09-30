package org.wyf.game.logic.thread;

import org.wyf.game.logic.player.Players;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public abstract class ThreadPool {

    private static final Logger log = LoggerFactory.getLogger(ThreadPool.class);

    public static ExecutorService[] playerDBExecutors;

    public static ScheduledExecutorService scheduledExecutor;

    public static void start() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        playerDBExecutors = new ExecutorService[availableProcessors];
        for (int i = 0; i < playerDBExecutors.length; i++) {
            final int k = i;
            playerDBExecutors[i] = Executors.newSingleThreadExecutor(r -> new Thread(r, "player-db-thread-" + k));
        }
        scheduledExecutor = new ScheduledThreadPoolExecutor(5);
        scheduledExecutor.scheduleAtFixedRate(() -> log.debug("实时在线玩家数量{}", Players.getPlayers().size()), 10, 10, TimeUnit.SECONDS);
    }

    public static void close() {
        for (ExecutorService executor : ThreadPool.playerDBExecutors) {
            executor.close();
        }
        log.info("player db thread pool close");
    }

    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long delay, long period, TimeUnit unit) {
        return scheduledExecutor.scheduleAtFixedRate(runnable, delay, period, unit);
    }

    public static ScheduledExecutorService getScheduledExecutor() {
        return scheduledExecutor;
    }

    public static ExecutorService getPlayerDBExecutor(int playerId) {
        return playerDBExecutors[playerId % playerDBExecutors.length];
    }

    public static ExecutorService getPlayerExecutor(int playerId) {
        return Executors.newSingleThreadExecutor(Thread.ofVirtual().name("player-" + playerId).factory());
    }
}
