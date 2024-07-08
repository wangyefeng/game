package org.wangyefeng.game.gate.net.client;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.wangyefeng.game.gate.net.GateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author wangyefeng
 * @date 2024-07-05
 * @description 逻辑服务器的客户端
 */
public class LogicClient extends Client {

    private static final LogicClient instance = new LogicClient("127.0.0.1", 9999);

    public static LogicClient getInstance() {
        return instance;
    }

    public LogicClient(String host, int port) {
        super(host, port);
    }

    @Override
    public void init() {
        GateHandler handler = new GateHandler();
        EventLoopGroup group = new NioEventLoopGroup(1);
        bootstrap.group(group).channel(NioSocketChannel.class);
        HeartBeatHandler heartBeatHandler = new HeartBeatHandler(this);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline cp = ch.pipeline();
                cp.addLast(new IdleStateHandler(20, 5, 0, TimeUnit.SECONDS));
                cp.addLast(heartBeatHandler);
                cp.addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 4, 0, 4));
                cp.addLast(new LogicDecoder());
                cp.addLast(handler);
            }
        });
    }
}
