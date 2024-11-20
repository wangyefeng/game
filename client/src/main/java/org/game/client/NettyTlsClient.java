package org.game.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.security.KeyStore;

public class NettyTlsClient {
    public static void main(String[] args) throws Exception {
        // TrustStore 文件路径
        String truststorePath = "C:\\Users\\TU\\mytruststore.jks";
        String truststorePassword = "password";  // TrustStore 密码

        // 加载 TrustStore
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream(truststorePath)) {
            trustStore.load(fis, truststorePassword.toCharArray());
        }

        // 创建 TrustManagerFactory，并使用 TrustStore
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        // 获取 X509TrustManager
        X509TrustManager trustManager = null;
        for (javax.net.ssl.TrustManager tm : trustManagerFactory.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
                trustManager = (X509TrustManager) tm;
                break;
            }
        }

        if (trustManager == null) {
            throw new IllegalStateException("X509TrustManager not found in TrustManagerFactory");
        }

        // 创建 SSL/TLS 上下文
        SslContext sslContext = SslContextBuilder.forClient()
                .trustManager(trustManager)  // 设置信任的 TrustManager
                .build();

        // 配置线程池
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            // 添加 SSL/TLS 加密处理
                            ch.pipeline().addLast(sslContext.newHandler(ch.alloc(), "localhost", 8443));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                    System.out.println("Received: " + msg);
                                }
                            });
                        }
                    });

            // 启动客户端并连接到服务器
            ChannelFuture future = bootstrap.connect("localhost", 8443).sync();
            System.out.println("Netty TLS client connected to server");

            // 发送消息
            future.channel().writeAndFlush("Hello from TLS client!");

            // 等待客户端关闭
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
