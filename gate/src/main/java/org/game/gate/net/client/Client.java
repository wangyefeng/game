package org.game.gate.net.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public abstract class Client {

    private static final Logger log = LoggerFactory.getLogger(Client.class);

    protected String host;

    protected int port;

    protected Channel channel;

    protected Bootstrap bootstrap = new Bootstrap();

    protected boolean running;

    protected String name;

    protected EventLoopGroup eventLoopGroup;

    private Thread reconnectThread;

    public Client(String host, int port, String name) {
        Assert.hasLength(host, "host不能为空!");
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

    public void close() throws InterruptedException{
        if (reconnectThread != null) {
            reconnectThread.interrupt();
        }
        channel.close().sync();
        eventLoopGroup.shutdownGracefully().sync();
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void connect() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
                channel = channelFuture.channel();
                running = true;
                log.info("服务器连接成功！连接到服务器 {}", this);
                reconnectThread = null;
                break;
            } catch (InterruptedException e) {
                log.info("重连线程中断！");
                break;
            } catch (Exception e) {
                log.error("连接服务器失败，原因: {} 正在重试...", e.getMessage());
            }
        }
    }

    public void start() {
        init();
        connect();
    }

    public void reconnect() {
        reconnectThread = new Thread(() -> connect(), "reconnect");
        reconnectThread.start();
    }

    @Override
    public String toString() {
        return "{host='" + host + '\'' + ", port=" + port + ", name='" + name + '\'' + '}';
    }
}
