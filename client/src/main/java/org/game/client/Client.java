package org.game.client;

import com.fasterxml.jackson.core.type.TypeReference;
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
import org.game.common.util.JsonUtil;
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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

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
            ChannelFuture future = bootstrap.connect(host, RandomUtil.random(ports)).sync();
            System.out.println("Connected to server " + host + ":" + ports);

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
        Builder basedUrl = WebClient.builder().baseUrl("http://127.0.0.1/auth");
        for (int i = 1; i <= 10; i++) {
            int finalI = i;
            String token;
            int playerId;
            String loginResponse = basedUrl.build()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path("/login")
                            .queryParam("username", "user" + finalI) // 查询参数
                            .queryParam("password", "123456")
                            .build()) // 请求的 URI
                    .retrieve() // 发起请求
                    .bodyToMono(String.class).block();
            HttpResp<LoginResponse> httpResp = JsonUtil.parseJson(loginResponse, new TypeReference<>() {
            });
            if (httpResp.isSuccess()) {
                token = httpResp.getData().token();
                playerId = httpResp.getData().userId();
            } else {
                String registerResponse = basedUrl.build()
                        .get()
                        .uri(uriBuilder -> uriBuilder.path("/register")
                                .queryParam("username", "user" + finalI) // 查询参数
                                .queryParam("password", "123456")
                                .build()) // 请求的 URI
                        .retrieve() // 发起请求
                        .bodyToMono(String.class).block();
                HttpResp<?> registerResp = JsonUtil.parseJson(registerResponse, HttpResp.class);
                if (registerResp.isSuccess()) {
                    loginResponse = basedUrl.build()
                            .get()
                            .uri(uriBuilder -> uriBuilder.path("/login")
                                    .queryParam("username", "user" + finalI) // 查询参数
                                    .queryParam("password", "123456")
                                    .build()) // 请求的 URI
                            .retrieve() // 发起请求
                            .bodyToMono(String.class).block();
                    httpResp = JsonUtil.parseJson(loginResponse, new TypeReference<>() {
                    });
                    if (httpResp.isSuccess()) {
                        token = httpResp.getData().token();
                        playerId = httpResp.getData().userId();
                    } else {
                        log.error("登录失败：{}", httpResp.getMsg());
                        return;
                    }
                } else {
                    log.error("注册失败：{}", registerResp.getMsg());
                    return;
                }
            }
            log.info("登录成功，token：{}", token);
            run(playerId, token);
        }
    }
}
