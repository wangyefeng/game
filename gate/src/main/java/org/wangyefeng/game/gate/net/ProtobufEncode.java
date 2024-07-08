package org.wangyefeng.game.gate.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * gate与中心服务器以及跨服的编码器
 * @author 王叶峰
 * @date 2021年6月25日
 *
 */
@ChannelHandler.Sharable
public class ProtobufEncode extends MessageToByteEncoder<LogicMessage> {

	@Override
	protected void encode(ChannelHandlerContext ctx, LogicMessage msg, ByteBuf out) throws Exception {
		if (msg.getMessage() != null) {
			out.writeInt(0);
			out.writeInt(msg.getCode());
			ByteBufOutputStream outputStream = new ByteBufOutputStream(out);
			msg.getMessage().writeTo(outputStream);
			out.setInt(0, out.readableBytes() - 4);
		} else {
			out.writeInt(4);
			out.writeInt(msg.getCode());
		}
	}

}
