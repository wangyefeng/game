package org.wangyefeng.game.logic.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TcpServer {

    private static final Logger log = LoggerFactory.getLogger(TcpServer.class);

    public static final int FRAME_LENGTH = 4; // 帧长度字节长度

    public static final int PROTOCOL_TYPE_LENGTH = 1; // 协议体类型字节长度

    public static final int CODE_LENGTH = 2; // 消息号字节长度

    public static final int PLAYER_ID_LENGTH = 4; // 玩家ID字节长度

    public static final int MIN_FRAME_LENGTH = PROTOCOL_TYPE_LENGTH + CODE_LENGTH + PLAYER_ID_LENGTH; // 数据字节长度

    public static final int MAX_FRAME_LENGTH = 1024 * 10; // 最大帧长度

    private Channel channel;

    private boolean isRunning = false;

    TcpServer() {
    }

    public void start(int port) {
        log.info("tcp server is starting...");
        if (isRunning) {
            throw new IllegalStateException("Server is already running");
        }
        EventLoopGroup bossGroup = new NioEventLoopGroup(1); // 用于接收客户端连接
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // 用于处理客户端连接
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            ClientHandler clientHandler = new ClientHandler();
            ClientMsgEncode clientMsgEncode = new ClientMsgEncode();
            GateHandler gateHandler = new GateHandler();
            bootstrap.group(bossGroup, workerGroup).channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new ReadTimeoutHandler(20));
                    pipeline.addLast(new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, 0, FRAME_LENGTH, 0, FRAME_LENGTH));
                    pipeline.addLast(new Decode());
                    pipeline.addLast(clientMsgEncode);
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
            channel = future.channel();
            isRunning = true;
            log.info("tcp server started and listening on port {}", port);
            channel.closeFuture().addListener((ChannelFutureListener) channelFuture -> {
                isRunning = false;
                log.warn("tcp server stopped");
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            });
        } catch (Exception e) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            throw new RuntimeException(e);
        }
    }

}
