package org.wyf.game.tools.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.wyf.game.proto.MessageCode;
import org.wyf.game.proto.Topic;
import org.wyf.game.proto.protocol.*;
import org.wyf.game.proto.struct.Login;

import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

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
        ctx.executor().schedule(() -> {
            ctx.channel().writeAndFlush(MessageCode.of(ClientToGateProtocol.AUTH, Login.PbAuthReq.newBuilder().setToken(token).build()));
        }, 1, TimeUnit.SECONDS);

        ctx.executor().scheduleAtFixedRate(() -> {
            log.info("player: {}, ping", playerId);
            ctx.channel().writeAndFlush(MessageCode.of(ClientToGateProtocol.PING));
        }, 5, 5, TimeUnit.SECONDS);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        msg.readByte();
        byte from = msg.readByte();
        short code = msg.readShort();
        Protocol protocol = Protocols.getProtocol(from, Topic.CLIENT.getCode(), code);
        Assert.notNull(protocol, "No protocol found for from: " + from + ", code: " + code);
        ByteBufInputStream inputStream = new ByteBufInputStream(msg);
        if (protocol.equals(GateToClientProtocol.PLAYER_TOKEN_VALIDATE)) {
            Login.PbAuthResp loginResponse = Login.PbAuthResp.parseFrom(inputStream);
            if (loginResponse.getSuccess()) {
                if (loginResponse.getIsRegistered()) {
                    ctx.channel().writeAndFlush(MessageCode.of(ClientToLogicProtocol.LOGIN, Login.PbLoginReq.newBuilder().build()));
                } else {
                    ctx.channel().writeAndFlush(MessageCode.of(ClientToLogicProtocol.REGISTER, Login.PbRegisterReq.newBuilder().setName("test" + playerId).build()));
                }
                ctx.executor().scheduleAtFixedRate(() -> {
                    ctx.channel().writeAndFlush(MessageCode.of(ClientToLogicProtocol.LEVEL_UP));
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
