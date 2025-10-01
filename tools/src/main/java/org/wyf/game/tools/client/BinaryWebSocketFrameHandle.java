package org.wyf.game.tools.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wyf.game.proto.MessageCode;
import org.wyf.game.proto.Topic;
import org.wyf.game.proto.protocol.*;
import org.wyf.game.proto.struct.Login;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * gate对client处理器
 *
 * @author wangyf
 * @date 2021年1月26日
 */
public class BinaryWebSocketFrameHandle extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {

    private static final Logger log = LoggerFactory.getLogger(BinaryWebSocketFrameHandle.class);

    private int playerId;

    private final String token;


    public BinaryWebSocketFrameHandle(String token) {
        this.token = token;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().eventLoop().schedule(() -> {
            ctx.channel().writeAndFlush(new BinaryWebSocketFrame(create(MessageCode.of(ClientToGateProtocol.AUTH, Login.PbAuthReq.newBuilder().setToken(token).build()))));
        }, 1, TimeUnit.SECONDS);
        ctx.executor().scheduleAtFixedRate(() -> ctx.writeAndFlush(new PingWebSocketFrame()), 5, 5, TimeUnit.SECONDS);
    }

    public static ByteBuf create(MessageCode<?> msg) {
        byte[] array = msg.data().toByteArray();
        // 直接内存
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(4 + array.length);
        buffer.writeByte(1);
        buffer.writeByte(msg.protocol().to().getCode());
        buffer.writeShort(msg.getCode());
        buffer.writeBytes(array);
        return buffer;
    }

    /**
     * 读取消息通道
     *
     * @throws Exception
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame msg) throws Exception {
        ByteBuf in = msg.content();
        in.readByte();
        byte from = in.readByte();
        byte to = Topic.CLIENT.getCode();
        short code = in.readShort();
        Protocol protocol = Protocols.getProtocol(from, to, code);
        if (protocol == null) {
            log.error("未知协议: from: {}, to: {}, code: {}", from, to, code);
            return;
        }

        InputStream inputStream = new ByteBufInputStream(in);
        log.info("玩家: {}, 收到协议: {}", playerId, protocol);
        switch (protocol) {
            case GateToClientProtocol.PLAYER_TOKEN_VALIDATE -> {
                Login.PbAuthResp pbAuthResp = Login.PbAuthResp.parseFrom(inputStream);
                if (pbAuthResp.getSuccess()) {
                    playerId = pbAuthResp.getPlayerId();
                    if (pbAuthResp.getIsRegistered()) {
                        ctx.channel().writeAndFlush(MessageCode.of(ClientToLogicProtocol.LOGIN));
                    } else {
                        ctx.channel().writeAndFlush(MessageCode.of(ClientToLogicProtocol.REGISTER, Login.PbRegisterReq.newBuilder().setName("test" + playerId).build()));
                    }
                }
            }
            default -> {
                // ignore
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        log.error("", cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.warn("服务器断开连接！！！");
    }
}
