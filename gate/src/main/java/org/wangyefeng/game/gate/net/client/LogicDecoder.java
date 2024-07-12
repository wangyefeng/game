package org.wangyefeng.game.gate.net.client;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.wangyefeng.game.gate.player.Player;
import org.wangyefeng.game.gate.player.Players;
import org.wangyefeng.game.gate.protocol.LogicProtocol;
import org.wangyefeng.game.gate.thread.ThreadPool;
import org.wangyefeng.game.proto.MessageCode;

import java.util.List;

public class LogicDecoder extends ByteToMessageDecoder {

    private static final Logger log = LoggerFactory.getLogger(LogicDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte type = in.readByte();// 协议类型
        short code = in.readShort();
        switch (type) {
            case 0 -> {
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

            }
            case 1 -> { // 网关处理消息
                Assert.isTrue(LogicProtocol.match(code), "Invalid code: " + code);
                int length = in.readableBytes();
                if (length > 0) {
                    ByteBufInputStream inputStream = new ByteBufInputStream(in);
                    Message message = (Message) LogicProtocol.getParser(code).parseFrom(inputStream);
                    out.add(new MessageCode<>(code, message));
                } else {
                    out.add(new MessageCode<>(code));
                }
            }
        }
    }
}
