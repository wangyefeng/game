package org.game.logic.thread;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerActorBehavior extends AbstractBehavior<PlayerActorBehavior.Command> {

    private final static Logger log = LoggerFactory.getLogger(PlayerActorBehavior.class);

    public PlayerActorBehavior(ActorContext<Command> context) {
        super(context);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder().onMessage(PlayerAction.class, this::onPlayerMessage).onMessage(ShutdownMsg.class, this::onShutdown).build();
    }

    private Behavior<Command> onShutdown(ShutdownMsg shutdownMsg) {
        getContext().getLog().debug("{} actor shutdown", getContext().getSelf().path().name());
        return Behaviors.stopped();
    }

    private Behavior<Command> onPlayerMessage(PlayerAction msg) {
        try {
            msg.run();
        } catch (Exception e) {
            log.error("player action error", e);
        }
        return this;
    }

    public interface Command {
    }

    public enum ShutdownMsg implements Command {
        INSTANCE;
    }

    public interface PlayerAction extends Command, Runnable {
    }
}
