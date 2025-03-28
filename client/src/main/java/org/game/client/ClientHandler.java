package org.game.client;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.game.proto.MessageCode;
import org.game.proto.protocol.ClientToGateProtocol;
import org.game.proto.protocol.ClientToLogicProtocol;
import org.game.proto.protocol.GateToClientProtocol;
import org.game.proto.struct.Login;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<MessageCode<?>> {

    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);

    private final int playerId;

    private final String token;

    public ClientHandler(int playerId, String token) {
        this.playerId = playerId;
        this.token = token;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ctx.channel().writeAndFlush(new MessageCode<>(ClientToGateProtocol.AUTH, Login.PbAuthReq.newBuilder().setPlayerId(playerId).setToken(token).build()));
        ctx.executor().scheduleAtFixedRate(() -> {
            log.info("player: {}, ping", playerId);
            ctx.channel().writeAndFlush(new MessageCode<>(ClientToGateProtocol.PING));
        }, 5, 5, TimeUnit.SECONDS);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageCode<?> message) {
        log.info("player: {}, Received message: {}", playerId, message);
        Message data = message.getData();
        if (message.getProtocol().equals(GateToClientProtocol.PLAYER_TOKEN_VALIDATE)) {
            Login.PbAuthResp loginResponse = (Login.PbAuthResp) data;
            if (loginResponse.getSuccess()) {
                if (loginResponse.getIsRegistered()) {
                    ctx.channel().writeAndFlush(new MessageCode<>(ClientToLogicProtocol.LOGIN, Login.PbLoginReq.newBuilder().build()));
                } else {
                    ctx.channel().writeAndFlush(new MessageCode<>(ClientToLogicProtocol.REGISTER, Login.PbRegisterReq.newBuilder().setName("test" + playerId).build()));
                }
                ctx.executor().scheduleAtFixedRate(() -> {
                    ctx.channel().writeAndFlush(new MessageCode<>(ClientToLogicProtocol.LEVEL_UP));
                }, 5, 5, TimeUnit.SECONDS);
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.info("玩家{}已断开连接", playerId);
    }
}
