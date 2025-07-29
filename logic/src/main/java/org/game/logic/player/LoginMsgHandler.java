package org.game.logic.player;

import akka.actor.typed.ActorRef;
import io.netty.channel.Channel;
import org.game.logic.GameService;
import org.game.logic.actor.Action;
import org.game.logic.actor.PlayerAction;
import org.game.logic.actor.PlayerActorService;
import org.game.logic.net.AbstractPlayerMsgHandler;
import org.game.logic.net.ChannelKeys;
import org.game.proto.protocol.ClientToLogicProtocol;
import org.game.proto.protocol.LogicToClientProtocol;
import org.game.proto.struct.Login;
import org.game.proto.struct.Login.PbLoginReq;
import org.game.proto.struct.Login.PbLoginResp;
import org.game.proto.struct.Login.PbLoginResp.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class LoginMsgHandler extends AbstractPlayerMsgHandler<PbLoginReq> {


    private static final Logger log = LoggerFactory.getLogger(LoginMsgHandler.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PlayerActorService playerActorService;

    @Override
    public void handle0(Channel channel, int playerId, Login.PbLoginReq data) {
        log.info("玩家{}登录游戏", playerId);
        ActorRef<Action> playerActor = playerActorService.createActor(playerId);
        playerActor.tell((PlayerAction) () -> {
            Player player = Players.getPlayer(playerId);
            if (player == null) {
                player = new Player(playerId, applicationContext.getBeansOfType(GameService.class).values(), channel, playerActor);
                PlayerService playerService = player.getService(PlayerService.class);
                if (!playerService.playerExists()) {
                    log.warn("玩家登录失败，玩家 {}不存在", playerId);
                    return;
                }
                player.login(data);
                Players.addPlayer(player);
                channel.attr(ChannelKeys.PLAYERS_KEY).get().add(player.getId());
            } else {
                player.setChannel(channel);
            }
            Builder resp = PbLoginResp.newBuilder();
            resp.setIsNew(false);
            player.loginResp(resp);
            player.writeToClient(LogicToClientProtocol.LOGIN, resp.build());
        });
    }

    @Override
    public ClientToLogicProtocol getProtocol() {
        return ClientToLogicProtocol.LOGIN;
    }
}
