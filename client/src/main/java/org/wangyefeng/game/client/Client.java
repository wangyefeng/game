package org.wangyefeng.game.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class Client {

    private final String host;
    private final int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            ClientHandler handler = new ClientHandler();
            bootstrap.group(group)
                     .channel(NioSocketChannel.class)
                     .handler(new ChannelInitializer<SocketChannel>() {
                         @Override
                         protected void initChannel(SocketChannel ch) throws Exception {
                             ChannelPipeline pipeline = ch.pipeline();
                             pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                             pipeline.addLast(new ProtobufCodec());
                             pipeline.addLast(handler);
                         }
                     });

            // 连接到服务器
            ChannelFuture future = bootstrap.connect(host, port).sync();
            System.out.println("Connected to server " + host + ":" + port);

            // 等待连接关闭
            future.channel().closeFuture().sync();
        } finally {
            // 关闭 EventLoopGroup，释放所有资源
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new Client("127.0.0.1", 8888).run();
    }
}
