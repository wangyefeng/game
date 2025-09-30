package org.wyf.game.gate.net.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

public abstract class Client {

    private static final Logger log = LoggerFactory.getLogger(Client.class);

    private static final int RECONNECT_TIME = 5;

    private final String id;

    protected String host;

    protected int port;

    protected Channel channel;

    protected Bootstrap bootstrap = new Bootstrap();

    protected String name;

    protected EventLoopGroup eventLoopGroup;

    public Client(String id, String host, int port, String name) {
        Assert.hasLength(host, "host不能为空!");
        Assert.isTrue(port > 0, "端口号必须大于0!");
        this.id = id;
        this.host = host;
        this.port = port;
        this.name = name;
    }

    public abstract void init();

    public Channel getChannel() {
        return channel;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void close() {
        log.info("关闭客户端连接： {}", this);
        if (!eventLoopGroup.isShutdown()) {
            eventLoopGroup.shutdownGracefully();
            log.info("关闭客户端连接完成！");
        }
    }

    public void connect() {
        if (isClose()) {
            log.info("连接中断，尝试重新连接！");
            return;
        }
        bootstrap.connect(host, port).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("连接成功！连接到服务器 {}", this);
                channel = future.channel();
                channel.closeFuture().addListener((ChannelFutureListener) _ -> reconnect());
            } else {
                reconnect();
            }
        });
    }

    public void start() {
        init();
        connect();
    }

    public void reconnect() {
        eventLoopGroup.schedule(this::connect, RECONNECT_TIME, TimeUnit.SECONDS);
    }

    @Override
    public String toString() {
        return "{id=" + id + ", host='" + host + '\'' + ", port=" + port + ", name='" + name + '\'' + '}';
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public boolean isClose() {
        return eventLoopGroup.isShutdown();
    }
}
