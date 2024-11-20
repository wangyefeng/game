package org.game.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class NettyClient {

    public static void main(String[] args) throws Exception {
        // 事件循环组
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            // 设置 SSL 上下文，客户端会验证服务器的证书
            InputStream inputStream = NettyClient.class.getClassLoader().getResourceAsStream("cert.pem");
            SslContext sslContext = SslContextBuilder.forClient()
                    .trustManager(inputStream) // 客户端通过这个证书验证服务器
                    .build();

            // 创建 Bootstrap
            Bootstrap b = new Bootstrap();
            b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // 添加 SSL/TLS 处理器，客户端连接时会进行证书验证
                        ch.pipeline().addFirst(new SslHandler(sslContext.newEngine(ch.alloc())));
                        // 添加编码器和解码器
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new ClientHandler()); // 自定义客户端处理器
                    }
                });

            // 连接到服务器
            ChannelFuture f = b.connect("game.wangyefeng.fun", 9090).sync();
            System.out.println("Client connected to server.");

            // 发送消息到服务器
            f.channel().writeAndFlush("Hello from client");
            f.channel().eventLoop().scheduleAtFixedRate(() -> {
                f.channel().writeAndFlush("ping");
            }, 1000, 1000, TimeUnit.MILLISECONDS);
            // 等待客户端连接关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅地关闭线程池
            group.shutdownGracefully();
        }
    }

    // 自定义的客户端处理类
    private static class ClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            // 打印服务器返回的消息
            System.out.println("Received from server: " + msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
}


