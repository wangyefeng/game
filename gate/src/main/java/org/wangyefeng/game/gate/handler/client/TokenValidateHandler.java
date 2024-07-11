package org.wangyefeng.game.gate.handler.client;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wangyefeng.game.gate.net.AttributeKeys;
import org.wangyefeng.game.gate.player.Player;
import org.wangyefeng.game.gate.player.Players;
import org.wangyefeng.game.gate.protocol.ClientProtocol;
import org.wangyefeng.game.gate.protocol.ToClientProtocol;
import org.wangyefeng.game.gate.thread.ThreadPool;
import org.wangyefeng.game.proto.MessageCode;
import org.wangyefeng.game.proto.struct.Common;

@Component
public class TokenValidateHandler implements ClientMsgHandler<Common.PbInt> {

    private static final Logger log = LoggerFactory.getLogger(TokenValidateHandler.class);

    @Override
    public void handle(Channel channel, Common.PbInt msg) {
        if (channel.hasAttr(AttributeKeys.PLAYER)) {
            log.warn("Player {} has already logged in.", channel.attr(AttributeKeys.PLAYER).get());
            return;
        }
        int playerId = msg.getVal();
        Players.lock.writeLock().lock();
        try {
            Player player = Players.getPlayer(playerId);
            if (player == null) {
                player = new Player(playerId, channel, ThreadPool.next());
                channel.attr(AttributeKeys.PLAYER).set(player);
                Players.addPlayer(player);
            } else {
                Channel oldCh = player.getChannel();
                player.setChannel(channel);
                channel.attr(AttributeKeys.PLAYER).set(player);
                oldCh.eventLoop().execute(() -> {
                    if (oldCh.isActive()) {
                        oldCh.writeAndFlush(new MessageCode<>(ToClientProtocol.KICK_OUT, Common.PbInt.newBuilder().setVal(1).build()));
                        oldCh.close();
                    }
                });
            }
        } finally {
            Players.lock.writeLock().unlock();
        }
    }

    @Override
    public ClientProtocol getProtocol() {
        return ClientProtocol.TOKEN_VALIDATE;
    }
}
