package org.game.client;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class NettyTlsServer {
    public static void main(String[] args) throws Exception {
        // 密钥库文件路径
        String keystorePath = "C:\\Users\\TU\\mykeystore.jks";
        String keystorePassword = "password";  // 密钥库密码

        // 加载密钥库
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream(keystorePath)) {
            keyStore.load(fis, keystorePassword.toCharArray());
        }

        // 提取证书链和私钥
        String alias = "myalias"; // 证书的别名
        char[] password = keystorePassword.toCharArray();

        // 提取证书链
        Certificate[] chain = keyStore.getCertificateChain(alias);

        // 提取私钥
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password);

        // 确保证书链不为空
        if (chain == null || privateKey == null) {
            throw new Exception("Failed to retrieve certificate chain or private key");
        }

        // 创建 SSL/TLS 上下文
        SslContext sslContext = SslContextBuilder.forServer(privateKey, (X509Certificate) chain[0]).build();

        // 配置线程池
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            // 添加 SSL/TLS 加密处理
                            ch.pipeline().addLast(sslContext.newHandler(ch.alloc()));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                    System.out.println("Received: " + msg);
                                    ctx.writeAndFlush("Hello from TLS Server!");
                                }
                            });
                        }
                    });

            // 启动服务器
            ChannelFuture future = serverBootstrap.bind(8443).sync();
            System.out.println("Netty TLS server started on port 8443");

            // 等待服务器关闭
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
