package org.game.logic.net;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.game.proto.CodeMsgHandler;
import org.game.proto.MessageCode;
import org.game.proto.MsgHandler;
import org.game.proto.MsgHandlerFactory;
import org.game.proto.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.naming.OperationNotSupportedException;

/**
 * 处理消息的handler
 *
 * @author wangyefeng
 */
@ChannelHandler.Sharable
@Component
public class TcpCodeMsgHandler extends SimpleChannelInboundHandler<MessageCode<?>> {

    private static final Logger log = LoggerFactory.getLogger(TcpCodeMsgHandler.class);

    @Autowired
    private MsgHandlerFactory msgHandlerFactory;

    @Override
    @SuppressWarnings("unchecked")
    protected void channelRead0(ChannelHandlerContext ctx, MessageCode<?> message) throws OperationNotSupportedException {
        MsgHandler<? extends Message> handler = msgHandlerFactory.getHandler(message.getProtocol());
        if (handler == null) {
            log.error("协议未定义处理器：{}", Protocol.toString(message.getProtocol()));
            return;
        }
        if (!(handler instanceof CodeMsgHandler codeMsgHandler)) {
            log.error("协议处理器类型不匹配：{}", Protocol.toString(message.getProtocol()));
            return;
        }
        try {
            codeMsgHandler.handle(ctx.channel(), message.getData());
        } catch (Exception e) {
            log.error("协议处理失败 message：{}", message, e);
        }
    }
}
