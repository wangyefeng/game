package org.wangyefeng.game.gate.net.client;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wangyefeng.game.gate.player.Player;
import org.wangyefeng.game.gate.player.Players;
import org.wangyefeng.game.gate.thread.ThreadPool;
import org.wangyefeng.game.proto.MessageCode;
import org.wangyefeng.game.proto.Topic;
import org.wangyefeng.game.proto.protocol.Protocol;
import org.wangyefeng.game.proto.protocol.ProtocolUtils;

import java.util.List;

public class LogicDecoder extends ByteToMessageDecoder {

    private static final Logger log = LoggerFactory.getLogger(LogicDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte from = Topic.LOGIC.getCode();
        byte type = in.readByte();// 协议类型
        byte to = in.readByte();
        short code = in.readShort();
        Protocol protocol = ProtocolUtils.getProtocol(from, code);
        if (protocol == null || protocol.to().getCode() != to) {
            log.warn("收到非法消息协议 from:{}, to:{}, code:{}", from, to, code);
            in.skipBytes(in.readableBytes());
            return;
        }
        if (to == Topic.GATE.getCode()) {
            int length = in.readableBytes();
            if (length > 0) {
                ByteBufInputStream inputStream = new ByteBufInputStream(in);
                Message message = (Message) protocol.parser().parseFrom(inputStream);
                out.add(new MessageCode<>(protocol, message));
            } else {
                out.add(new MessageCode<>(protocol));
            }
        } else if (to == Topic.CLIENT.getCode()) {
            log.info("转发消息协议");
            int playerId = in.readInt(); // 玩家id
            ThreadPool.getPlayerExecutor(playerId).execute(() -> {
                Player player = Players.getPlayer(playerId);
                if (player != null) {
                    int readableBytes = in.readableBytes();
                    ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer(readableBytes + 7, readableBytes + 7);
                    buffer.writeInt(readableBytes + 3);
                    buffer.writeByte(type);
                    buffer.writeShort(code);
                    buffer.writeBytes(in);
                    player.getChannel().writeAndFlush(buffer);
                } else {
                    log.info("转发消息失败，玩家已经离线code:{}, playerId:{}", code, playerId);
                }
            });
        } else {
            log.warn("收到非法消息协议 to:{}", to);
        }
    }
}
