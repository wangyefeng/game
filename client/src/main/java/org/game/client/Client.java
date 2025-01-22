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
import org.game.common.http.HttpResp;
import org.game.common.random.RandomUtil;
import org.game.proto.CodeMsgEncode;
import org.game.proto.CommonDecoder;
import org.game.proto.MessageCode;
import org.game.proto.MessageCodeDecoder;
import org.game.proto.MessagePlayerDecoder;
import org.game.proto.Topic;
import org.game.proto.protocol.Protocol;
import org.game.proto.protocol.Protocols;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;

@SpringBootApplication
public class Client implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Client.class);
    private final String host;
    private final int[] ports;

    @Value("${server.ssl.trust-certificate}")
    private String trustCertificate;

    @Value("${server.ssl.enabled}")
    private boolean sslEnabled;

    @Autowired
    private ResourceLoader resourceLoader;

    private SslContext sslContext;

    public Client() {
        this.host = "localhost";
        this.ports = new int[]{8888};
    }

    public Client(String host, int[] ports) {
        this.host = host;
        this.ports = ports;
    }

    public void run(int playerId, String token) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup(1);
        // 设置 SSL 上下文，客户端会验证服务器的证书
        if (sslEnabled) {
            log.info("开启TLS/SSL加密！！！");
            File trustCertificateFile = resourceLoader.getResource(trustCertificate).getFile();
            sslContext = SslContextBuilder.forClient().trustManager(trustCertificateFile).build();
        }
        try {
            Bootstrap bootstrap = new Bootstrap();
            ClientHandler handler = new ClientHandler(playerId, token);
            MessageToByteEncoder<MessageCode<?>> encode = new CodeMsgEncode();
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
            ChannelFuture future = bootstrap.connect(host, RandomUtil.random(ports)).sync();
            log.info("Connected to server {}:{}", host, ports);

            // 等待连接关闭
            future.channel().closeFuture().addListener(_ -> {
                // 关闭 EventLoopGroup，释放所有资源
                group.shutdownGracefully();
            });
        } catch (Exception e) {
            // 关闭 EventLoopGroup，释放所有资源
            group.shutdownGracefully();
            throw e;
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Client.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Protocols.init();
        WebClient client = WebClient.builder().baseUrl("http://game.wangyefeng.fun/auth").build();
        int num = 1;
        for (int i = 1; i <= num; i++) {
            String username = "user" + i;
            String password = "123456";
            HttpResp<LoginResponse> loginResponse = login(client, username, password);
            if (!loginResponse.isSuccess() && loginResponse.code() == 1) {// 用户不存在，尝试注册
                HttpResp<Void> registerResp = register(client, username, password);
                if (!registerResp.isSuccess()) {
                    throw new RuntimeException("注册失败：" + registerResp.msg());
                }
                loginResponse = login(client, username, password);
            }
            if (!loginResponse.isSuccess()) {
                throw new RuntimeException("登录失败：" + loginResponse.msg());
            }
            String token = loginResponse.data().token();
            int playerId = loginResponse.data().userId();
            log.info("登录成功，token：{}", token);
            run(playerId, token);
        }
    }

    private static HttpResp<LoginResponse> login(WebClient client, String username, String password) {
        return client.get().uri(uriBuilder -> uriBuilder.path("/login").queryParam("username", username).queryParam("password", password).build()).retrieve().bodyToMono(new ParameterizedTypeReference<HttpResp<LoginResponse>>() {}).block();
    }

    private static HttpResp<Void> register(WebClient client, String username, String password) {
        return client.get().uri(uriBuilder -> uriBuilder.path("/register").queryParam("username", username).queryParam("password", password).build()).retrieve().bodyToMono(new ParameterizedTypeReference<HttpResp<Void>>() {}).block();
    }
}
