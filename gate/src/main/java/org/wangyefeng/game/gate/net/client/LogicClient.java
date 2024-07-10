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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.concurrent.TimeUnit;

/**
 * @author wangyefeng
 * @date 2024-07-05
 * @description 逻辑服务器的客户端
 */
@Component
@ConfigurationProperties(prefix = "client.logic")
@Validated
public class LogicClient extends Client {

    public LogicClient() {
        super();
        this.name = "logic";
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
                cp.addLast(new IdleStateHandler(20, 5, 0, TimeUnit.SECONDS));
                cp.addLast(heartBeatHandler);
                cp.addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 4, 0, 4));
                cp.addLast(new LogicDecoder());
                cp.addLast(handler);
            }
        });
    }

    @Override
    public void setPort(int port) {
        super.setPort(port);
    }

    @Override
    public void setHost(String host) {
        super.setHost(host);
    }

    @NotBlank
    @Override
    public String getHost() {
        return super.getHost();
    }

    @Min(1025)
    @Max(65535)
    @Override
    public int getPort() {
        return super.getPort();
    }
}


