package org.game.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.game.proto.CodeMsgEncode;
import org.game.proto.CommonDecoder;
import org.game.proto.MessageCodeDecoder;
import org.game.proto.MessagePlayerDecoder;
import org.game.proto.Topic;
import org.game.proto.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ResourceLoader;

import java.io.File;

@SpringBootApplication
public class Client implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Client.class);
    private final String host;
    private final int port;

    @Value("${server.ssl.trust-certificate}")
    private String trustCertificate;

    @Value("${server.ssl.enabled}")
    private boolean sslEnabled;

    @Autowired
    private ResourceLoader resourceLoader;

    private SslContext sslContext;

    public Client() {
        this.host = "localhost";
        this.port = 8888;
    }

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run(int playerId) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup(1);
        // 设置 SSL 上下文，客户端会验证服务器的证书
        if (sslEnabled) {
            log.info("开启TLS/SSL加密！！！");
            File trustCertificateFile = resourceLoader.getResource(trustCertificate).getFile();
            sslContext = SslContextBuilder.forClient().trustManager(trustCertificateFile).build();
        }
        try {
            Bootstrap bootstrap = new Bootstrap();
            ClientHandler handler = new ClientHandler(playerId);
            MessageToByteEncoder encode = new CodeMsgEncode();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            if (sslEnabled) {
                                pipeline.addFirst(sslContext.newHandler(ch.alloc()));
                            }
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, Protocol.FRAME_LENGTH, 0, Protocol.FRAME_LENGTH));
                            CommonDecoder commonDecoder = new CommonDecoder(Topic.CLIENT.getCode());
                            commonDecoder.registerDecoder(new MessageCodeDecoder());
                            commonDecoder.registerDecoder(new MessagePlayerDecoder());
                            pipeline.addLast(commonDecoder);
                            pipeline.addLast(encode);
                            pipeline.addLast(handler);
                        }
                    });

            // 连接到服务器
            ChannelFuture future = bootstrap.connect(host, port).sync();
            System.out.println("Connected to server " + host + ":" + port);

            // 等待连接关闭
            future.channel().closeFuture().addListener(_ -> {
                // 关闭 EventLoopGroup，释放所有资源
                group.shutdownGracefully();
            });
        } catch (Exception e) {
            // 关闭 EventLoopGroup，释放所有资源
            group.shutdownGracefully();
        }
    }


    public static void main(String[] args) {
        SpringApplication.run(Client.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        for (int i = 1; i < 5; i++) {
            run(i);
        }
    }
}
