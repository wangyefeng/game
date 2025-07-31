package org.game.logic.net;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.game.proto.MessagePlayer;
import org.game.proto.MsgHandler;
import org.game.proto.MsgHandlerFactory;
import org.game.proto.PlayerMsgHandler;
import org.game.proto.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 玩家消息处理器
 *
 * @author wangyefeng
 */
@ChannelHandler.Sharable
@Component
public class TcpPlayerMsgHandler extends SimpleChannelInboundHandler<MessagePlayer<?>> {

    @Autowired
    private MsgHandlerFactory msgHandlerFactory;

    private static final Logger log = LoggerFactory.getLogger(TcpPlayerMsgHandler.class);

    @Override
    @SuppressWarnings("unchecked")
    protected void channelRead0(ChannelHandlerContext ctx, MessagePlayer<?> message) {
        MsgHandler<? extends Message> handler = msgHandlerFactory.getHandler(message.getProtocol());
        if (handler == null) {
            log.error("协议未定义处理器：{}", Protocol.toString(message.getProtocol()));
            return;
        }
        if (!(handler instanceof PlayerMsgHandler playerMsgHandler)) {
            log.error("协议处理器类型不匹配：{}", Protocol.toString(message.getProtocol()));
            return;
        }
        try {
            playerMsgHandler.handle(ctx.channel(), message.getPlayerId(), message.getData());
        } catch (Exception e) {
            log.error("处理消息异常：{}", message, e);
        }
    }

}
