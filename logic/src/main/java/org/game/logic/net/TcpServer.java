package org.game.logic.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.Nonnull;
import org.game.proto.CommonDecoder;
import org.game.proto.MessageCodeDecoder;
import org.game.proto.MessagePlayerDecoder;
import org.game.proto.PlayerMsgEncode;
import org.game.proto.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class TcpServer {

    private static final Logger log = LoggerFactory.getLogger(TcpServer.class);

    public static final int FRAME_LENGTH = 4; // 帧长度字节长度

    public static final int MAX_FRAME_LENGTH = 1024 * 10; // 最大帧长度

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

    private final int port;

    private boolean isRunning = false;

    private NioEventLoopGroup group;

    public TcpServer(@Value("${logic.tcp-port}") int port) {
        this.port = port;
    }

    public void start() {
        if (isRunning) {
            throw new IllegalStateException("Server is already running");
        }
        group = new NioEventLoopGroup();// 默认线程数量 2 * cpu核心数
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            TcpHandler tcpHandler = new TcpHandler();
            PlayerMsgEncode playerMsgEncode = new PlayerMsgEncode();
            HeartBeatHandler heartBeatHandler = new HeartBeatHandler();
            bootstrap.group(group).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(@Nonnull SocketChannel ch) {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new IdleStateHandler(READER_IDLE_TIME, WRITER_IDLE_TIME, 0, TimeUnit.SECONDS));// 设置读超时时间为20秒
                    pipeline.addLast(heartBeatHandler);
                    pipeline.addLast(new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, 0, FRAME_LENGTH, 0, FRAME_LENGTH));
                    CommonDecoder commonDecoder = new CommonDecoder(Topic.LOGIC.getCode());
                    commonDecoder.registerDecoder(new MessageCodeDecoder());
                    commonDecoder.registerDecoder(new MessagePlayerDecoder());
                    pipeline.addLast(commonDecoder);
                    pipeline.addLast(tcpHandler);
                    pipeline.addLast(playerMsgEncode);
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
            bootstrap.bind(port).sync();
            isRunning = true;
            log.info("tcp server started and listening on port {}", port);
        } catch (Exception e) {
            group.shutdownGracefully();
            throw new RuntimeException("TCP服务器启动失败 端口：" + port, e);
        }
    }

    public int getPort() {
        return port;
    }

    public void close() throws InterruptedException {
        if (!isRunning) {
            return;
        }
        group.shutdownGracefully().sync();
        isRunning = false;
        log.info("tcp server closed");
    }
}
