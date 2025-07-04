package org.game.logic.thread;

import akka.Done;
import akka.actor.CoordinatedShutdown;
import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.MailboxSelector;
import akka.actor.typed.javadsl.Behaviors;
import org.game.logic.player.Players;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public abstract class ThreadPool {

    private static final Logger log = LoggerFactory.getLogger(ThreadPool.class);

    public static ThreadPoolExecutor[] playerDBExecutors;

    public static ScheduledExecutorService scheduledExecutor;

    public static void start() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        playerDBExecutors = new ThreadPoolExecutor[availableProcessors];
        for (int i = 0; i < playerDBExecutors.length; i++) {
            final int k = i;
            playerDBExecutors[i] = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> new Thread(r, "player-db-thread-" + k));
        }
        scheduledExecutor = new ScheduledThreadPoolExecutor(5);
        scheduledExecutor.scheduleAtFixedRate(() -> log.debug("实时在线玩家数量{}", Players.getPlayers().size()), 10, 10, TimeUnit.SECONDS);
    }

    public static void shutdown() {
        for (ThreadPoolExecutor executor : ThreadPool.playerDBExecutors) {
            executor.close();
        }
        log.info("player db thread pool shutdown");
    }

    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long delay, long period, TimeUnit unit) {
        return scheduledExecutor.scheduleAtFixedRate(runnable, delay, period, unit);
    }

    public static ThreadPoolExecutor getPlayerDBExecutor(int playerId) {
        return playerDBExecutors[playerId % playerDBExecutors.length];
    }

    public static ScheduledExecutorService getScheduledExecutor() {
        return scheduledExecutor;
    }
}
