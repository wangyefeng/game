package org.game.logic.actor;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class PlayerActorBehavior extends AbstractBehavior<Action> {

    public PlayerActorBehavior(ActorContext<Action> context) {
        super(context);
    }

    @Override
    public Receive<Action> createReceive() {
        return newReceiveBuilder().onMessage(PlayerAction.class, this::onPlayerMessage).onMessage(ShutdownAction.class, this::onShutdown).build();
    }

    private Behavior<Action> onShutdown(ShutdownAction action) {
        getContext().getLog().debug("player {} actor shutdown", getContext().getSelf().path().name());
        return Behaviors.stopped();
    }

    private Behavior<Action> onPlayerMessage(PlayerAction msg) {
        try {
            msg.run();
        } catch (Exception e) {
            getContext().getLog().error("player action error", e);
        }
        return this;
    }
}
