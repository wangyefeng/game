package org.game.gate.net.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.game.gate.player.Player;
import org.game.proto.protocol.Protocol;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author wangyefeng
 * @date 2024-07-05
 * @description 逻辑服务器的客户端
 */
public class LogicClient extends Client {

    /**
     * 读超时时间
     * 单位：秒
     * 默认：20
     */
    private static final int READER_IDLE_TIME = 20;

    /**
     * 写超时时间
     * 单位：秒
     * 默认：5
     */
    private static final int WRITER_IDLE_TIME = 5;

    /**
     * 所有消息的长度限制
     * 单位：字节
     * 默认：1MB
     */
    private static final int LENGTH_LIMIT = 1024 * 1024;

    private Set<Player> players = new HashSet<>();

    private ManagedChannel grpcChannel;

    public LogicClient(int id, String host, int port, int rpcPort) {
        super(id, host, port, "logic");
        grpcChannel = ManagedChannelBuilder.forAddress(host, rpcPort).usePlaintext().build();
    }

    @Override
    public void init() {
        ChannelHandler handler = new LogicHandler(this);
        eventLoopGroup = new NioEventLoopGroup(1);
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class);
        HeartBeatHandler heartBeatHandler = new HeartBeatHandler();
        GateCodeEncode gateCodeEncode = new GateCodeEncode();
        GatePlayerEncode gatePlayerEncode = new GatePlayerEncode();
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            public void initChannel(SocketChannel ch) {
                ChannelPipeline cp = ch.pipeline();
                cp.addLast(new IdleStateHandler(READER_IDLE_TIME, WRITER_IDLE_TIME, 0, TimeUnit.SECONDS));
                cp.addLast(heartBeatHandler);
                cp.addLast(new LengthFieldBasedFrameDecoder(LENGTH_LIMIT, 0, Protocol.FRAME_LENGTH, 0, Protocol.FRAME_LENGTH));
                cp.addLast(new LogicDecoder());
                cp.addLast(gateCodeEncode, gatePlayerEncode);// 自定义编码器
                cp.addLast(handler);
            }
        });
    }

    @Override
    public String getHost() {
        return super.getHost();
    }

    @Override
    public int getPort() {
        return super.getPort();
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public ManagedChannel getGrpcChannel() {
        return grpcChannel;
    }

    @Override
    public void close() throws InterruptedException {
        super.close();
        grpcChannel.shutdown();
    }
}


