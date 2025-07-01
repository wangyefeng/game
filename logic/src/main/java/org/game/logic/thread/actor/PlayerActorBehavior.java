package org.game.logic.thread.actor;

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

public class PlayerActorBehavior extends AbstractBehavior<PlayerActorMsg> {

    private final static Logger log = LoggerFactory.getLogger(PlayerActorBehavior.class);

    private final Map<Integer, ActorRef<Runnable>> playerActors = new HashMap<>();

    public PlayerActorBehavior(ActorContext<PlayerActorMsg> context) {
        super(context);
    }

    @Override
    public Receive<PlayerActorMsg> createReceive() {
        return newReceiveBuilder().onMessage(PlayerActorMsg.class, this::onPlayerMessage).build();
    }

    private Behavior<PlayerActorMsg> onPlayerMessage(PlayerActorMsg msg) {
        int playerId = msg.getPlayerId();
        if (msg.isOffline()) {
            log.debug("销毁actor player-{}", playerId);
            ActorRef<Runnable> remove = playerActors.remove(playerId);
            if (remove != null) {
                if (msg.getAction() != null) {
                    remove.tell(msg.getAction());
                }
                getContext().stop(remove);
            } else {
                log.warn("玩家{}离线, actor player-{} 不存在", playerId, playerId);
            }
        } else {
            ActorRef<Runnable> userActor = playerActors.computeIfAbsent(playerId, id -> {
                log.debug("创建actor player-{}", id);

                Behavior<Runnable> behavior = Behaviors.receive((_, action) -> {
                    try {
                        action.run();
                    } catch (Exception e) {
                        log.error("执行玩家{}的行为失败", playerId, e);
                    }
                    return Behaviors.same();
                });
                return getContext().spawn(behavior, "player-" + id);
            });
            userActor.tell(msg.getAction());
        }
        return this;
    }
}
