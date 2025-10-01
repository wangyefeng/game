package org.wyf.game.tools.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketCloseStatus;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.wyf.game.common.http.HttpResp;
import org.wyf.game.proto.protocol.Protocols;

import java.net.URI;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Client implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Client.class);

    @Value("${server.host}")
    private String host;

    @Value("${server.gate.port}")
    private int gatePort;

    @Value("${server.login.port}")
    private int loginPort;

    public Client() {
    }

    public void run(String token) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpClientCodec());
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            pipeline.addLast(new WebSocketClientProtocolHandler(
                                    URI.create("ws://" + host + ":" + gatePort + "/gate"),
                                    WebSocketVersion.V13,
                                    null,
                                    false,
                                    new DefaultHttpHeaders(),
                                    65536
                            ));
                            pipeline.addLast(new BinaryWebSocketFrameHandle(token));
                        }
                    });

            // 连接到服务器
            ChannelFuture future = bootstrap.connect(host, gatePort).sync();
            log.info("Connected to server {}:{}", host, gatePort);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    log.info("JVM 正在关闭，请等待...");
                    future.channel().writeAndFlush(new CloseWebSocketFrame(WebSocketCloseStatus.NORMAL_CLOSURE));
                    group.shutdownGracefully();
                } catch (Exception e) {
                    log.error("关闭服务器异常！", e);
                } finally {
                    log.info("JVM 已关闭！");
                }
            }, "shutdown-hook"));
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

    void main() {
        SpringApplication.run(Client.class);
    }

    @Override
    public void run(String... args) throws Exception {
        Protocols.init();
        WebClient client = WebClient.builder().baseUrl("http://" + host + ":" + loginPort + "/login/auth").build();
        int num = 3;
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
            log.info("登录成功，token：{}", token);
            run(token);
        }
    }

    private static HttpResp<LoginResponse> login(WebClient client, String username, String password) {
        return client.get().uri(uriBuilder -> uriBuilder.path("/login").queryParam("username", username).queryParam("password", password).build()).retrieve().bodyToMono(new ParameterizedTypeReference<HttpResp<LoginResponse>>() {
        }).block();
    }

    private static HttpResp<Void> register(WebClient client, String username, String password) {
        return client.get().uri(uriBuilder -> uriBuilder.path("/register").queryParam("username", username).queryParam("password", password).build()).retrieve().bodyToMono(new ParameterizedTypeReference<HttpResp<Void>>() {
        }).block();
    }

    public record LoginResponse(String token) {
    }
}
