package org.game.logic.player;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.game.common.event.Listener;
import org.game.common.event.PublishManager;
import org.game.common.event.Publisher;
import org.game.logic.service.GameService;
import org.game.logic.service.ItemService;
import org.game.logic.thread.ThreadPool;
import org.game.proto.MessagePlayer;
import org.game.proto.protocol.LogicToClientProtocol;
import org.game.proto.struct.Login;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Player {

    // 玩家id
    private final int id;

    /**
     * 服务模块集合
     */
    private final Map<Class<? extends GameService>, GameService> map = new HashMap<>();

    /**
     * 玩家的channel
     */
    private Channel channel;

    /**
     * 定时保存数据
     */
    private ScheduledFuture<?> saveFuture;

    /**
     * 监听器管理者
     */
    private PublishManager<PlayerEventType> publishManager = new PublishManager<>(PlayerEventType.values());

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

    public void asyncSave() {
        for (GameService gameService : map.values()) {
            gameService.asyncSave();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends GameService> T getService(Class<T> playerServiceClass) {
        return (T) map.get(playerServiceClass);
    }

    public void register(Login.PbRegister registerMsg) {
        for (GameService gameService : map.values()) {
            gameService.register(registerMsg);
        }
        init();
    }

    private void startSaveTimer() {
        saveFuture = scheduleAtFixedRate(this::asyncSave, 0, 1, TimeUnit.MINUTES);
    }

    public void login() {
        for (GameService gameService : map.values()) {
            gameService.load();
        }
        init();
    }

    private void init() {
        initListener();
        startSaveTimer();
    }

    private void initListener() {
        ItemService itemService = getService(ItemService.class);
        itemService.initListener();
    }

    public void logout() {
        saveFuture.cancel(true);
        asyncSave();
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        return ThreadPool.scheduleAtFixedRate(() -> ThreadPool.getPlayerExecutor(id).execute(runnable), initialDelay, period, unit);
    }

    public void updateEvent(PlayerEventType eventType, Object value) {
        publishManager.update(eventType, value);
    }

    public <T extends Publisher<?>> T getEventListeners(PlayerEventType eventType) {
        return publishManager.getEventListeners(eventType);
    }

    public void addEventListener(PlayerEventType eventType, Listener<?> listener) {
        publishManager.addEventListener(eventType, listener);
    }
}
