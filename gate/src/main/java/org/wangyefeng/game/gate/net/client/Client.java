package org.wangyefeng.game.gate.net.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Client {

    private static final Logger log = LoggerFactory.getLogger(Client.class);
    private final String host;

    private final int port;

    private Channel channel;

    protected Bootstrap bootstrap = new Bootstrap();

    private boolean running;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        init();
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
        running = false;
        ChannelFuture channelFuture = channel.close();
        try {
            channelFuture.sync();
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

    public void start() throws InterruptedException {
        while (true) {
            try {
                channel = bootstrap.connect(host, port).sync().channel();
                running = true;
                log.info("服务器连接成功！{}", channel);
                break;
            } catch (Exception e) {
                log.error("连接失败，msg: {} 正在重试...", e.getMessage());
                Thread.sleep(2000);
            }
        }
    }

    public void reconnect() {
        Thread thread = new Thread(() -> {
            try {
                start();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "logic reconnect");
        thread.start();
    }
}
