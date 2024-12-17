package org.game.logic.player;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.game.logic.service.GameService;
import org.game.logic.thread.ThreadPool;
import org.game.proto.MessagePlayer;
import org.game.proto.protocol.LogicToClientProtocol;
import org.game.proto.struct.Login;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Player {

    private static final Logger log = LoggerFactory.getLogger(Player.class);

    // 玩家id
    private int id;

    /**
     * 服务模块集合
     */
    private Map<Class<? extends GameService>, GameService> map = new HashMap<>();

    /**
     * 玩家的channel
     */
    private Channel channel;

    /**
     * 定时保存数据
     */
    private ScheduledFuture<?> saveFuture;

    public Player(int id, Collection<GameService> gameServices, Channel channel) {
        this.id = id;
        this.channel = channel;
        for (GameService gameService : gameServices) {
            gameService.setPlayer(this);
            map.put(gameService.getClass(), gameService);
        }
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public int getId() {
        return id;
    }

    public void sendToClient(LogicToClientProtocol protocol, Message message) {
        channel.writeAndFlush(new MessagePlayer<>(getId(), protocol, message));
    }

    public void sendToClient(LogicToClientProtocol protocol) {
        channel.writeAndFlush(new MessagePlayer<>(getId(), protocol));
    }

    public void load() {
        for (GameService gameService : map.values()) {
            gameService.load();
        }
    }

    public void save() {
        for (GameService gameService : map.values()) {
            gameService.copy();
        }
        ThreadPool.getPlayerDBExecutor(id).execute(() -> {
            long start = System.currentTimeMillis();
            for (GameService gameService : map.values()) {
                try {
                    gameService.save();
                } catch (Exception e) {
                    log.error("玩家{}保存数据失败，模块：{} 数据：{}", id, gameService.getClass().getSimpleName(), gameService.dataToString(), e);
                }
            }
            log.debug("玩家{}保存数据完成，耗时：{}毫秒", id, System.currentTimeMillis() - start);
        });
    }

    @SuppressWarnings("unchecked")
    public <T extends GameService> T getService(Class<T> playerServiceClass) {
        return (T) map.get(playerServiceClass);
    }

    public void register(Login.PbRegister registerMsg) {
        for (GameService gameService : map.values()) {
            gameService.init(registerMsg);
        }
        startSaveTimer();
    }

    private void startSaveTimer() {
        saveFuture = scheduleAtFixedRate(() -> save(), 0, 1, TimeUnit.MINUTES);
    }

    public void login() {
        load();
        startSaveTimer();
    }

    public void logout() {
        saveFuture.cancel(true);
        save();
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        return ThreadPool.scheduleAtFixedRate(() -> ThreadPool.getPlayerExecutor(id).execute(runnable), initialDelay, period, unit);
    }
}
