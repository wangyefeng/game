package org.wangyefeng.game.gate.net.client;


import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.wangyefeng.game.proto.protocol.Protocol;

import java.util.concurrent.TimeUnit;

/**
 * @author wangyefeng
 * @date 2024-07-05
 * @description 逻辑服务器的客户端
 */
@Component
@Validated
public class LogicClient extends Client {


    /**
     * 读超时时间
     * 单位：秒
     * 默认：20
     */
    private static final int READER_IDLE_TIME = 20;

    /**
     * 写超时时间
     * 单位：秒
     * 默认：5
     */
    private static final int WRITER_IDLE_TIME = 5;


    public LogicClient(String host, int port) {
        super(host, port, "logic");
    }

    @Override
    public void init() {
        ChannelHandler handler = new LogicHandler(this);
        EventLoopGroup group = new NioEventLoopGroup(1);
        bootstrap.group(group).channel(NioSocketChannel.class);
        HeartBeatHandler heartBeatHandler = new HeartBeatHandler(this);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline cp = ch.pipeline();
                cp.addLast(new IdleStateHandler(READER_IDLE_TIME, WRITER_IDLE_TIME, 0, TimeUnit.SECONDS));
                cp.addLast(heartBeatHandler);
                cp.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, Protocol.FRAME_LENGTH, 0, Protocol.FRAME_LENGTH));
                cp.addLast(new LogicDecoder());
                cp.addLast(new GateClientEncode());
                cp.addLast(handler);
            }
        });
    }

    @Override
    public String getHost() {
        return super.getHost();
    }

    @Override
    public int getPort() {
        return super.getPort();
    }
}


