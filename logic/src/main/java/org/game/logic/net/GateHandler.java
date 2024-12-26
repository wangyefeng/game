package org.game.logic.net;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.game.config.Configs;
import org.game.proto.MessageCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * @author wangyefeng
 * @date 2024-07-08
 * @description 处理消息的handler
 */
@ChannelHandler.Sharable
public class GateHandler extends SimpleChannelInboundHandler<MessageCode<?>> {

    private static final Logger log = LoggerFactory.getLogger(GateHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageCode<?> message) {
        Optional<GateMsgHandler<Message>> optionalHandler = GateMsgHandler.getHandler(message.getProtocol().getCode());
        if (optionalHandler.isEmpty()) {
            log.warn("非法协议号: {}", message.getProtocol());
            return;
        }
        optionalHandler.get().handle(ctx.channel(), message.getMessage(), Configs.getInstance());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("网络连接异常：", cause);
        if (ctx.channel().isActive()) {
            ctx.channel().close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.info("与客户端连接断开, 地址：{}", ctx.channel().remoteAddress());
    }
}
