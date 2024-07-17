package org.wangyefeng.game.gate.net.client;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wangyefeng.game.gate.player.Player;
import org.wangyefeng.game.gate.player.Players;
import org.wangyefeng.game.gate.thread.ThreadPool;
import org.wangyefeng.game.proto.DecoderType;
import org.wangyefeng.game.proto.MessageCode;
import org.wangyefeng.game.proto.Topic;
import org.wangyefeng.game.proto.protocol.Protocol;
import org.wangyefeng.game.proto.protocol.Protocols;

import java.util.List;

public class LogicDecoder extends ByteToMessageDecoder {

    private static final Logger log = LoggerFactory.getLogger(LogicDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte type = in.readByte();// 协议类型
        if (type == DecoderType.MESSAGE_CODE.getCode()) {
            byte from = Topic.LOGIC.getCode();
            byte to = in.readByte();
            short code = in.readShort();
            Protocol protocol = Protocols.getProtocol(from, to, code);
            if (protocol == null || protocol.to().getCode() != to) {
                error(from, to, code);
                in.skipBytes(in.readableBytes());
                return;
            }
            if (to == Topic.GATE.getCode()) {
                if (protocol.parser() != null) {
                    ByteBufInputStream inputStream = new ByteBufInputStream(in);
                    Message message = (Message) protocol.parser().parseFrom(inputStream);
                    out.add(new MessageCode<>(protocol, message));
                } else {
                    out.add(new MessageCode<>(protocol));
                }
            } else {
                error(from, to, code);
                in.skipBytes(in.readableBytes());
            }
        } else if (type == DecoderType.MESSAGE_PLAYER.getCode()) {
            byte from = Topic.LOGIC.getCode();
            byte to = in.readByte();
            int playerId = in.readInt();
            short code = in.readShort();
            Protocol protocol = Protocols.getProtocol(from, to, code);
            if (protocol == null || protocol.to().getCode() != to) {
                error(from, to, code);
                in.skipBytes(in.readableBytes());
                return;
            }
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
                        buffer.writeInt(duplicate.readableBytes() + 2);
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
                error(from, to, code);
                in.skipBytes(in.readableBytes());
            }
        }
    }

    private static void error(byte from, byte to, short code) {
        log.error("收到非法消息协议 from:{}, to:{}, code:{}", from, to, code);
    }
}
