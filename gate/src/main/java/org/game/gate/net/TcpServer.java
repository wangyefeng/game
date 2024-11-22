package org.game.gate.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.game.gate.net.client.LogicClient;
import org.game.proto.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TcpServer {

    private static final Logger log = LoggerFactory.getLogger(TcpServer.class);

    private boolean isRunning = false;

    @Value("${server.tcp-port:8888}")
    private int port;

    @Autowired
    private LogicClient logicClient;

    @Autowired
    private SslConfig sslConfig;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    TcpServer() {
    }

    public void start() {
        log.info("tcp server is starting...");
        if (isRunning) {
            throw new IllegalStateException("Server is already running");
        }
        bossGroup = new NioEventLoopGroup(1); // 用于接收客户端连接
        workerGroup = new NioEventLoopGroup(); // 用于读写流处理
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            ClientHandler clientHandler = new ClientHandler(logicClient);
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    ChannelPipeline pipeline = ch.pipeline();
                    if (sslConfig.isEnabled()) {
                        pipeline.addFirst(sslConfig.getSslContext().newHandler(ch.alloc()));
                    }
                    pipeline.addLast(new ReadTimeoutHandler(20));
                    pipeline.addLast(new LengthFieldBasedFrameDecoder(1024 * 10, 0, Protocol.FRAME_LENGTH, 0, Protocol.FRAME_LENGTH));
                    pipeline.addLast(new TcpCodec(logicClient));
                    pipeline.addLast(clientHandler);
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
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            throw new RuntimeException(e);
        }
    }

    public int getPort() {
        return port;
    }

    public void close() throws InterruptedException {
        if (!isRunning) {
            return;
        }
        bossGroup.shutdownGracefully().sync();
        workerGroup.shutdownGracefully().sync();
        isRunning = false;
        log.info("tcp server closed");
    }
}
