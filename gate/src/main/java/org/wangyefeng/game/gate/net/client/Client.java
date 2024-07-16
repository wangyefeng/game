package org.wangyefeng.game.gate.net.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
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

    public void close() {
        ChannelFuture channelFuture = channel.close();
        try {
            channelFuture.sync();
            running = false;
            log.info("断开服务器连接！ {}", this);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void connect() {
        while (true) {
            try {
                ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
                channel = channelFuture.channel();
                running = true;
                log.info("服务器连接成功！连接到服务器 {}", this);
                break;
            } catch (Exception e) {
                log.error("连接失败，msg: {} 正在重试...", e.getMessage());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    public void start() {
        init();
        connect();
    }

    public void reconnect() {
        Thread thread = new Thread(this::connect, "reconnect");
        thread.start();
    }

    @Override
    public String toString() {
        return "{host='" + host + '\'' + ", port=" + port + ", name='" + name + '\'' + '}';
    }

    protected void setHost(String host) {
        this.host = host;
    }

    protected void setPort(int port) {
        this.port = port;
    }
}
