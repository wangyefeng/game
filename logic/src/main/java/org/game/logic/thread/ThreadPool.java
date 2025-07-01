package org.game.logic.thread;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import org.game.logic.player.Players;
import org.game.logic.thread.actor.PlayerActorBehavior;
import org.game.logic.thread.actor.PlayerActorMsg;
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

    public static ThreadPoolExecutor[] playerDBExecutors;

    private final static ActorSystem<PlayerActorMsg> PLAYER_ACTOR_SYSTEM = ActorSystem.create(Behaviors.setup(PlayerActorBehavior::new), "player-actor");

    public static ScheduledExecutorService scheduledExecutor;


    public static void start() {
        playerDBExecutors = new ThreadPoolExecutor[EXECUTOR_SIZE];
        for (int i = 0; i < EXECUTOR_SIZE; i++) {
            final int k = i;
            playerDBExecutors[i] = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> new Thread(r, "player-db-thread-" + k));
        }
        scheduledExecutor = new ScheduledThreadPoolExecutor(5);
        scheduledExecutor.scheduleAtFixedRate(() -> log.debug("实时在线玩家数量{}", Players.getPlayers().size()), 10, 10, TimeUnit.SECONDS);
    }

    public static void shutdown() {
        PLAYER_ACTOR_SYSTEM.terminate();
        for (ThreadPoolExecutor executor : ThreadPool.playerDBExecutors) {
            executor.close();
        }
        log.info("thread pool shutdown");
    }

    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long delay, long period, TimeUnit unit) {
        return scheduledExecutor.scheduleAtFixedRate(runnable, delay, period, unit);
    }

    public static void executePlayerAction(int playerId, Runnable action) {
        PLAYER_ACTOR_SYSTEM.tell(new PlayerActorMsg(playerId, action));
    }

    public static void closePlayerActor(int playerId) {
        PLAYER_ACTOR_SYSTEM.tell(new PlayerActorMsg(playerId, null, true));
    }

    public static ThreadPoolExecutor getPlayerDBExecutor(int playerId) {
        return playerDBExecutors[playerId % EXECUTOR_SIZE];
    }

    public static ScheduledExecutorService getScheduledExecutor() {
        return scheduledExecutor;
    }
}
