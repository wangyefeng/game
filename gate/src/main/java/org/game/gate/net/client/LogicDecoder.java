package org.game.gate.net.client;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.game.gate.player.Player;
import org.game.gate.player.Players;
import org.game.gate.thread.ThreadPool;
import org.game.proto.*;
import org.game.proto.protocol.Protocol;
import org.game.proto.protocol.Protocols;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LogicDecoder extends ByteToMessageDecoder {

    private static final Logger log = LoggerFactory.getLogger(LogicDecoder.class);

    private final MsgHandlerFactory msgHandlerFactory;

    public LogicDecoder(MsgHandlerFactory msgHandlerFactory) {
        this.msgHandlerFactory = msgHandlerFactory;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte type = in.readByte();// 协议类型
        byte from, to;
        short code;
        final int playerId;
        if (type == DecoderType.MESSAGE_CODE.getCode()) {
            playerId = 0;
            from = Topic.LOGIC.getCode();
            to = in.readByte();
            code = in.readShort();
        } else if (type == DecoderType.MESSAGE_PLAYER.getCode()) {
            from = Topic.LOGIC.getCode();
            to = in.readByte();
            playerId = in.readInt();
            code = in.readShort();
        } else {
            throw new IllegalArgumentException("非法的协议类型：" + type);
        }
        Protocol protocol = Protocols.getProtocol(from, to, code);
        if (protocol == null) {
            log.error("收到非法消息协议 from:{}, to:{}, code:{}", from, to, code);
            in.skipBytes(in.readableBytes());
            return;
        }

        if (type == DecoderType.MESSAGE_CODE.getCode()) {
            if (to == Topic.GATE.getCode()) {
                ByteBufInputStream inputStream = new ByteBufInputStream(in);
                Message message = msgHandlerFactory.getHandler(protocol).parseFrom(inputStream);
                out.add(MessageCode.of(protocol, message));
            } else {
                log.warn("目前不支持 消息类型：MESSAGE_CODE 转发消息给{} 敬请期待！", to);
                in.skipBytes(in.readableBytes());
            }
        } else {
            if (to == Topic.CLIENT.getCode()) {
                ByteBuf duplicate = in.retainedDuplicate();
                ThreadPool.getPlayerExecutor(playerId).execute(() -> {
                    Player player = Players.getPlayer(playerId);
                    if (player == null) {
                        duplicate.release();
                        log.info("转发消息失败，玩家已经离线code:{}, playerId:{}", code, playerId);
                        return;
                    }
                    ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer(8, 8);
                    try {
                        log.debug("转发消息给客户端 playerId:{}, 协议名:{}, 协议长度:{}", playerId, protocol, duplicate.readableBytes());
                        buffer.writeInt(duplicate.readableBytes() + 4);
                        buffer.writeByte(DecoderType.MESSAGE_CODE.getCode());
                        buffer.writeByte(from);
                        buffer.writeShort(code);
                        ByteBuf byteBuf = new CompositeByteBuf(PooledByteBufAllocator.DEFAULT, true, 2, buffer, duplicate);
                        player.getChannel().writeAndFlush(byteBuf);
                    } catch (Exception e) {
                        buffer.release();
                        duplicate.release();
                        throw e;
                    }
                });
                in.skipBytes(in.readableBytes());
            } else {
                log.warn("目前不支持 消息类型：MESSAGE_PLAYER 转发消息给{} 敬请期待！", to);
                in.skipBytes(in.readableBytes());
            }
        }
    }
}
