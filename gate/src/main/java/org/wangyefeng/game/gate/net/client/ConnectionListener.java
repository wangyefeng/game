package org.wangyefeng.game.gate.net.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 重连事件监听器
 * @author wangyf
 * @date 2021年1月26日
 */
public class ConnectionListener implements ChannelFutureListener {
	
	private Client client;
	
	public ConnectionListener(Client client) {
		this.client = client;
	}
	
	private static Logger logger = LoggerFactory.getLogger(ConnectionListener.class);

	@Override
	public void operationComplete(ChannelFuture channelFuture) throws Exception {
		if (!channelFuture.isSuccess()) {
			logger.info("服务器连接出错：{}:{} 开始重连", client.getHost(), client.getPort());
			final EventLoop loop = channelFuture.channel().eventLoop();
			loop.schedule(()-> client.connect(), 2L, TimeUnit.SECONDS);
		} else {
			client.setRunning(true);
			logger.info("服务端连接成功...{}:{}", client.getHost(), client.getPort());
		}
	}
}
