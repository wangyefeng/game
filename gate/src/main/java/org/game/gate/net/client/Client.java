package org.game.gate.net.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import org.game.common.event.Listener;
import org.game.common.event.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public abstract class Client {

    private static final Logger log = LoggerFactory.getLogger(Client.class);

    private final String id;

    protected String host;

    protected int port;

    protected Channel channel;

    protected Bootstrap bootstrap = new Bootstrap();

    protected String name;

    protected EventLoopGroup eventLoopGroup;

    protected Publisher<String> closePublisher = new Publisher<>();

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

    public void close() throws InterruptedException {
        log.info("关闭客户端连接： {}", this);
        if (!eventLoopGroup.isShutdown()) {
            eventLoopGroup.shutdownGracefully();
            log.info("关闭客户端连接完成！");
        }
        closePublisher.update(getId());
    }

    public void connect() {
        int retryCount = 3;
        for (int i = 0; i < retryCount; i++) {
            try {
                ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
                channel = channelFuture.channel();
                log.info("服务器连接成功！连接到服务器 {}", this);
                break;
            } catch (InterruptedException e) {
                log.info("重连线程被中断！{} 停止重连......", this);
                break;
            } catch (Exception e) {
                log.error("连接服务器失败，正在重试...", e);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    log.info("重连线程被中断！{} 停止重连...", this);
                    break;
                }
            }
        }
    }

    public void start() {
        init();
        connect();
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

    public void addListener(Listener<String> listener) {
        closePublisher.addListener(listener);
    }
}
