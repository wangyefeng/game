package org.game.logic.player;

import akka.actor.typed.ActorRef;
import io.netty.channel.Channel;
import org.game.config.Configs;
import org.game.logic.GameService;
import org.game.logic.actor.Action;
import org.game.logic.actor.Command;
import org.game.logic.net.AbstractPlayerMsgHandler;
import org.game.logic.actor.PlayerActorBehavior;
import org.game.logic.actor.PlayerActorService;
import org.game.proto.protocol.ClientToLogicProtocol;
import org.game.proto.protocol.LogicToClientProtocol;
import org.game.proto.struct.Login;
import org.game.proto.struct.Login.PbLoginResp;
import org.game.proto.struct.Login.PbLoginResp.Builder;
import org.game.proto.struct.Login.PbRegisterReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class RegisterMsgHandler extends AbstractPlayerMsgHandler<PbRegisterReq> {


    private static final Logger log = LoggerFactory.getLogger(RegisterMsgHandler.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PlayerActorService playerActorService;

    @Override
    public void handle0(Channel channel, int playerId, Login.PbRegisterReq data, Configs config) {
        log.info("玩家{}注册 信息: {}", playerId, data);
        ActorRef<Command> playerActor = playerActorService.createActor(playerId);
        playerActor.tell((Action) (() -> {
            Player player = Players.getPlayer(playerId);
            if (player != null) {
                log.info("玩家{}已经存在，不能重复注册", playerId);
                return;
            }
            player = new Player(playerId, applicationContext.getBeansOfType(GameService.class).values(), channel, playerActor);
            PlayerService playerService = player.getService(PlayerService.class);
            if (playerService.playerExists()) {
                log.info("玩家{}已经存在，不能重复注册", playerId);
                return;
            }
            player.register(data);
            Players.addPlayer(player);
            Builder resp = PbLoginResp.newBuilder();
            resp.setIsNew(true);
            player.loginResp(resp);
            player.writeToClient(LogicToClientProtocol.LOGIN, resp.build());
        }));
    }

    @Override
    public ClientToLogicProtocol getProtocol() {
        return ClientToLogicProtocol.REGISTER;
    }
}
