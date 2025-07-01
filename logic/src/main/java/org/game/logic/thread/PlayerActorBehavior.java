package org.game.logic.thread;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class PlayerActorBehavior extends AbstractBehavior<PlayerActorBehavior.Command> {

    private final static Logger log = LoggerFactory.getLogger(PlayerActorBehavior.class);

    public interface Command {
    }

    public record ShutdownMsg(int playerId) implements Command {
    }

    public record PlayerActorMsg(int playerId, Runnable action) implements Command {
    }

    private final Map<Integer, ActorRef<Runnable>> playerActors = new HashMap<>();

    public PlayerActorBehavior(ActorContext<Command> context) {
        super(context);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder().onMessage(PlayerActorMsg.class, this::onPlayerMessage).onMessage(ShutdownMsg.class, this::onShutdown).build();
    }

    private Behavior<Command> onShutdown(ShutdownMsg shutdownMsg) {
        int playerId = shutdownMsg.playerId;
        log.debug("销毁actor player-{}", playerId);
        ActorRef<Runnable> remove = playerActors.remove(playerId);
        if (remove != null) {
            getContext().stop(remove);
        } else {
            log.warn("玩家{}离线, actor player-{} 不存在", playerId, playerId);
        }
        return this;
    }

    private Behavior<Command> onPlayerMessage(PlayerActorMsg msg) {
        int playerId = msg.playerId();
        ActorRef<Runnable> userActor = playerActors.computeIfAbsent(playerId, id -> {
            log.debug("创建actor player-{}", id);
            Behavior<Runnable> behavior = Behaviors.receive((_, action) -> {
                try {
                    action.run();
                } catch (Exception e) {
                    log.error("执行玩家{}逻辑出现异常", playerId, e);
                }
                return Behaviors.same();
            });
            return getContext().spawn(behavior, "player-" + id);
        });
        userActor.tell(msg.action());
        return this;
    }
}
