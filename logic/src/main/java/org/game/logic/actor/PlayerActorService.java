package org.game.logic.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.MailboxSelector;
import akka.actor.typed.javadsl.Behaviors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class PlayerActorService {

    private static final Logger log = LoggerFactory.getLogger(PlayerActorService.class);

    private final ActorSystem<Void> playerActorSystem = ActorSystem.create(Behaviors.empty(), "player");

    public ActorRef<Action> createActor(int playerId) {
        return playerActorSystem.systemActorOf(Behaviors.setup(PlayerActorBehavior::new), String.valueOf(playerId), MailboxSelector.defaultMailbox());
    }

    public void close() throws Exception {
        log.info("player actor system starting shutdown");
        playerActorSystem.terminate();
        playerActorSystem.getWhenTerminated().toCompletableFuture().get(10L, TimeUnit.MINUTES);
        log.info("player actor system shutdown");
    }
}
