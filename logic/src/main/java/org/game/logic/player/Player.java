package org.game.logic.player;

import akka.actor.typed.ActorRef;
import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.game.common.event.Listener;
import org.game.common.event.PublishManager;
import org.game.common.event.Publisher;
import org.game.common.util.Assert;
import org.game.config.Configs;
import org.game.config.entity.CfgItem;
import org.game.config.entity.Item;
import org.game.config.entity.PlayerEvent;
import org.game.config.service.CfgItemService;
import org.game.logic.actor.Action;
import org.game.logic.actor.PlayerAction;
import org.game.logic.actor.ShutdownAction;
import org.game.logic.database.entity.PlayerInfo;
import org.game.logic.net.ChannelKeys;
import org.game.logic.player.item.*;
import org.game.logic.thread.ThreadPool;
import org.game.proto.MessagePlayer;
import org.game.proto.protocol.LogicToClientProtocol;
import org.game.proto.protocol.LogicToGateProtocol;
import org.game.proto.struct.Login;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

public class Player {

    private static final Logger log = LoggerFactory.getLogger(Player.class);
    // 玩家id
    private final int id;

    /**
     * 服务模块集合
     */
    private final Map<Class<? extends GameService<?>>, GameService<?>> map = new HashMap<>();

    private final Map<ItemType, Addable> addableService = new HashMap<>();

    private final Map<ItemType, Consumable> consumableService = new HashMap<>();

    /**
     * 玩家的channel
     */
    private Channel channel;

    /**
     * 定时保存数据
     */
    private ScheduledFuture<?> saveFuture;

    /**
     * 离线时间
     */
    private long logoutTime = 0;

    /**
     * 监听器管理者
     */
    private final PublishManager<PlayerEvent> publishManager = new PublishManager<>(PlayerEvent.values());

    private final List<DailyReset> dailyResetServices = new ArrayList<>();

    private final ActorRef<Action> actor;

    private Future<?> logoutFuture;

    /**
     * 数据库异步执行器
     */
    private final Executor dbExecutor;

    /**
     * 离线卸载数据时间 单位：秒
     */
    private static final long DESTROY_TIME = 10;

    /**
     * 保存间隔 单位：秒
     */
    private static final long SAVE_INTERVAL = 20;

    public Player(int id, Collection<GameService> gameServices, Channel channel, ActorRef<Action> actor) {
        this.id = id;
        this.channel = channel;
        initService(gameServices);
        this.actor = actor;
        ExecutorService[] playerDBExecutors = ThreadPool.getPlayerDBExecutors();
        this.dbExecutor = playerDBExecutors[id % playerDBExecutors.length];
    }

    private void initService(Collection<GameService> gameServices) {
        for (GameService<?> gameService : gameServices) {
            gameService.setPlayer(this);
            map.put((Class<? extends GameService<?>>) gameService.getClass(), gameService);
            if (gameService instanceof Addable addable) {
                addableService.put(addable.getType(), addable);
                if (gameService instanceof Consumable consumable) {
                    consumableService.put(consumable.getType(), consumable);
                }
            }
            if (gameService instanceof DailyReset dailyReset) {
                dailyResetServices.add(dailyReset);
            }
        }
    }

    public Channel getChannel() {
        return channel;
    }

    public int getId() {
        return id;
    }

    public void writeToClient(LogicToClientProtocol protocol, Message message) {
        channel.writeAndFlush(MessagePlayer.of(getId(), protocol, message));
    }

    public void writeToClient(LogicToClientProtocol protocol) {
        channel.writeAndFlush(MessagePlayer.of(getId(), protocol));
    }

    public void writeToGate(LogicToGateProtocol protocol, Message message) {
        channel.writeAndFlush(MessagePlayer.of(getId(), protocol, message));
    }

    public void writeToGate(LogicToGateProtocol protocol) {
        channel.writeAndFlush(MessagePlayer.of(getId(), protocol));
    }

    public void asyncSave(boolean cacheEvict) {
        for (GameService<?> gameService : map.values()) {
            gameService.asyncSave(cacheEvict);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends GameService<?>> T getService(Class<T> playerServiceClass) {
        return (T) map.get(playerServiceClass);
    }

    public void register(Login.PbRegisterReq registerMsg) {
        map.values().forEach(service -> service.register(registerMsg));
        init();
    }

    private void startSaveTimer() {
        saveFuture = scheduleAtFixedRate(() -> asyncSave(false), 0, SAVE_INTERVAL, TimeUnit.SECONDS);
    }

    public void loginResp(Login.PbLoginResp.Builder loginResp) {
        map.values().forEach(service -> service.loginResp(loginResp));
    }

    public void login(Login.PbLoginReq loginMsg, Channel channel, boolean isReconnect) {
        this.channel = channel;
        if (!isReconnect) {
            map.values().forEach(GameService::load);
            init();
            dailyReset(false);
        }
        logoutTime = 0;
        log.info("玩家{}登录游戏成功", getId());
    }

    private void init() {
        map.values().forEach(GameService::init);
        map.values().forEach(GameService::afterInit);
        startSaveTimer();
    }

    public void logout() {
        log.info("玩家{}退出游戏", getId());
        logoutTime = System.currentTimeMillis();
        channel.attr(ChannelKeys.PLAYERS_KEY).get().remove((Integer) getId());
        channel = null;
        if (logoutFuture == null) {
            logoutFuture = ThreadPool.getScheduledExecutor().schedule(new DestroyTask(), DESTROY_TIME, TimeUnit.SECONDS);
        }
    }

    private void destroy() {
        log.info("玩家{}销毁", getId());
        saveFuture.cancel(true);
        asyncSave(true);
        Players.removePlayer(getId());
        actor.tell(ShutdownAction.INSTANCE);
        logoutFuture = null;
    }

    public class DestroyTask implements Runnable {

        @Override
        public void run() {
            execute(() -> {
                if (isOnline()) {
                    logoutFuture = null;
                    return;
                }
                if (System.currentTimeMillis() - logoutTime < DESTROY_TIME * 1000) {
                    ThreadPool.getScheduledExecutor().schedule(this, System.currentTimeMillis() - logoutTime, TimeUnit.MILLISECONDS);
                } else {
                    Player.this.destroy();
                }
            });
        }
    }

    public ScheduledFuture<?> scheduleAtFixedRate(PlayerAction action, long initialDelay, long period, TimeUnit unit) {
        return ThreadPool.scheduleAtFixedRate(() -> execute(action), initialDelay, period, unit);
    }

    public void updateEvent(PlayerEvent eventType, Object value) {
        publishManager.update(eventType, value);
    }

    public void unloadListener(PlayerEvent eventType, Listener<?> listener) {
        publishManager.unloadListener(eventType, listener);
    }

    public <T extends Publisher<?>> T getListeners(PlayerEvent eventType) {
        return publishManager.getEventListeners(eventType);
    }

    public void addListener(PlayerEvent eventType, Listener<?> listener) {
        publishManager.addListener(eventType, listener);
    }

    public final void addItems(Item... items) {
        for (Item item : items) {
            addItem(item);
        }
    }

    public <T extends Item> void addItems(Collection<T> items) {
        for (Item item : items) {
            addItem(item);
        }
    }

    public void addItem(int itemId, long num) {
        CfgItemService cfgItemService = Configs.of(CfgItemService.class);
        CfgItem cfg = cfgItemService.getCfg(itemId);
        ItemType type = ItemType.getType(cfg.getType());
        Addable addable = addableService.get(type);
        if (addable == null) {
            throw new IllegalArgumentException("addable item type not found: " + type);
        }
        addable.add(itemId, num);
    }

    public void addItem(Item item) {
        addItem(item.id(), item.num());
    }

    public boolean itemsEnough(Item... items) {
        Collection<AddableItem> mergedItems = ItemUtil.mergeItems(items);
        return mergedItemsEnough(mergedItems);
    }

    public <T extends Item> boolean itemsEnough(Collection<T> items) {
        Collection<AddableItem> mergedItems = ItemUtil.mergeItems(items);
        return mergedItemsEnough(mergedItems);
    }

    private boolean mergedItemsEnough(Collection<? extends Item> mergedItems) {
        for (Item item : mergedItems) {
            if (!itemEnough(item)) {
                return false;
            }
        }
        return true;
    }

    public boolean itemEnough(Item item) {
        CfgItemService cfgItemService = Configs.of(CfgItemService.class);
        CfgItem cfg = cfgItemService.getCfg(item.id());
        ItemType type = ItemType.getType(cfg.getType());
        Consumable consumable = consumableService.get(type);
        if (consumable == null) {
            throw new IllegalArgumentException("consumable item type not found: " + type);
        }
        return !consumable.enough(item);
    }

    private void consumeItem(Item item) {
        CfgItemService cfgItemService = Configs.of(CfgItemService.class);
        CfgItem cfg = cfgItemService.getCfg(item.id());
        ItemType type = ItemType.getType(cfg.getType());
        Consumable consumable = consumableService.get(type);
        if (consumable == null) {
            throw new IllegalArgumentException("consumable item type not found: " + type);
        }
        consumable.consume(item);
    }

    private void checkItemsEnough(Collection<? extends Item> items) {
        for (Item item : items) {
            Assert.isTrue(itemEnough(item), "item not enough, id" + item.id() + " num:" + item.num());
        }
    }


    public void consumeItems(Item... items) {
        Collection<AddableItem> mergeItems = ItemUtil.mergeItems(items);
        checkItemsEnough(mergeItems);
        for (Item item : mergeItems) {
            consumeItem(item);
        }
    }

    public void consumeItems(Collection<Item> items) {
        Collection<AddableItem> mergeItems = ItemUtil.mergeItems(items);
        checkItemsEnough(mergeItems);
        for (Item item : mergeItems) {
            consumeItem(item);
        }
    }

    public void dailyReset(boolean isSend) {
        LocalDate now = LocalDate.now();
        PlayerService playerService = getService(PlayerService.class);
        PlayerInfo playerInfo = playerService.getEntity();
        LocalDate dailyResetDate = playerInfo.getDailyResetDate();
        if (now.isEqual(dailyResetDate)) {
            return;
        }
        playerInfo.setDailyResetDate(now);
        dailyResetServices.forEach(dailyReset -> dailyReset.reset(dailyResetDate, isSend));
    }

    public void execute(PlayerAction action) {
        actor.tell(action);
    }

    public boolean isOnline() {
        return logoutTime == 0;
    }

    public boolean isOffline() {
        return !isOnline();
    }

    public void dbExecute(PlayerAction action) {
        dbExecutor.execute(action);
    }
}
