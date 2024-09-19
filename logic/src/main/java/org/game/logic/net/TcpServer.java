package org.game.logic.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.game.proto.CommonDecoder;
import org.game.proto.MessageCodeDecoder;
import org.game.proto.MessagePlayerDecoder;
import org.game.proto.PlayerMsgEncode;
import org.game.proto.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
@ConfigurationProperties(prefix = "tcp")
public class TcpServer {

    private static final Logger log = LoggerFactory.getLogger(TcpServer.class);

    public static final int FRAME_LENGTH = 4; // 帧长度字节长度

    public static final int MAX_FRAME_LENGTH = 1024 * 10; // 最大帧长度

    private String host;

    private int port;

    private Channel channel;

    private boolean isRunning = false;

    private EventLoopGroup group;

    TcpServer() {
    }

    public void start() throws UnknownHostException {
        if (isRunning) {
            throw new IllegalStateException("Server is already running");
        }
        if (host == null) {
            InetAddress localhost = InetAddress.getLocalHost();
            host = localhost.getHostAddress();
        }
        group = new NioEventLoopGroup();// 默认线程数量 2 * cpu核心数
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            ClientHandler clientHandler = new ClientHandler();
            PlayerMsgEncode playerMsgEncode = new PlayerMsgEncode();
            GateHandler gateHandler = new GateHandler();
            bootstrap.group(group, group).channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new ReadTimeoutHandler(20));// 设置读超时时间为20秒
                    pipeline.addLast(new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, 0, FRAME_LENGTH, 0, FRAME_LENGTH));
                    CommonDecoder commonDecoder = new CommonDecoder(Topic.LOGIC.getCode());
                    commonDecoder.registerDecoder(new MessageCodeDecoder());
                    commonDecoder.registerDecoder(new MessagePlayerDecoder());
                    pipeline.addLast(commonDecoder);
                    pipeline.addLast(playerMsgEncode);
                    pipeline.addLast(clientHandler);
                    pipeline.addLast(gateHandler);
                }
            });
            bootstrap.option(ChannelOption.SO_BACKLOG, 200);
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            bootstrap.option(ChannelOption.SO_REUSEADDR, true); // 允许端口复用

            bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, false);// 禁用tcp保活机制，自定义ping包检测tcp链接是否正常
            bootstrap.childOption(ChannelOption.TCP_NODELAY, true);// 禁用Nagle算法
            bootstrap.childOption(ChannelOption.SO_RCVBUF, 1024 * 128); // 设置接收缓冲区大小
            bootstrap.childOption(ChannelOption.SO_SNDBUF, 1024 * 128); // 设置发送缓冲区大小
            // 绑定端口并启动服务器
            ChannelFuture future = bootstrap.bind(port).sync();
            isRunning = true;
            channel = future.channel();
            log.info("tcp server started and listening on port {}", port);
        } catch (Exception e) {
            group.shutdownGracefully();
            throw new RuntimeException(e);
        }
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void close() throws InterruptedException {
        if (!isRunning) {
            return;
        }
        channel.close().sync();
        isRunning = false;
        group.shutdownGracefully();
        log.info("tcp server closed");
    }
}
