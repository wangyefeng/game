package org.game.client;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyServer {
    private final int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        // 创建事件循环组
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // 处理接受连接的线程组
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // 处理I/O的线程组

        try {
            // 创建 ServerBootstrap 实例，用来启动服务
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // 指定 NIO 传输类型
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 连接建立时的处理逻辑
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 在管道中添加解码器、编码器、处理器
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new ServerHandler());  // 自定义的业务处理逻辑
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // 设置连接队列大小
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // 保持连接活跃

            // 绑定端口，启动服务
            ChannelFuture f = b.bind(port).sync();
            System.out.println("Server started, listening on " + port);

            // 等待服务器监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            // 释放资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new NettyServer(9099).start();
    }
}

// 自定义的处理类
class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 打印接收到的消息
        System.out.println("Received from client: " + msg);
        // 回复客户端
        ctx.writeAndFlush("Hello from server");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
