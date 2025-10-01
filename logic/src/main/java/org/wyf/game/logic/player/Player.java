package org.wyf.game.logic.player;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.wyf.game.common.RedisKeys;
import org.wyf.game.common.event.Listener;
import org.wyf.game.common.event.PublishManager;
import org.wyf.game.common.event.Publisher;
import org.wyf.game.common.util.Assert;
import org.wyf.game.common.util.TimeUtil;
import org.wyf.game.config.Configs;
import org.wyf.game.config.entity.CfgItem;
import org.wyf.game.config.entity.Item;
import org.wyf.game.config.entity.PlayerEvent;
import org.wyf.game.config.service.CfgItemService;
import org.wyf.game.logic.database.entity.PlayerInfo;
import org.wyf.game.logic.player.item.*;
import org.wyf.game.logic.thread.ThreadPool;
import org.wyf.game.proto.MessagePlayer;
import org.wyf.game.proto.protocol.LogicToClientProtocol;
import org.wyf.game.proto.protocol.LogicToGateProtocol;
import org.wyf.game.proto.protocol.Protocol;
import org.wyf.game.proto.struct.Login;
import org.wyf.game.proto.util.ProtobufJsonUtil;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

public class Player {

    private static final Logger log = LoggerFactory.getLogger(Player.class);

    /**
     * 离线卸载数据时间 单位：秒
     */
    private static final long DESTROY_TIME = 10;

    /**
     * 保存间隔 单位：秒
     */
    private static final long SAVE_INTERVAL = 20;

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

    private Future<?> destroyFuture;

    /**
     * 数据库线程执行器
     */
    private final Executor dbExecutor;

    /**
     * 逻辑线程执行器
     */
    private final ExecutorService executor;

    public Player(int id, Collection<GameService> gameServices, Channel channel) {
        this.id = id;
        this.channel = channel;
        initService(gameServices);
        this.executor = ThreadPool.getPlayerExecutor(getId());
        this.dbExecutor = ThreadPool.getPlayerDBExecutor(getId());
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

    public void loginOrRegisterResp(Login.PbLoginOrRegisterResp.Builder loginResp) {
        map.values().forEach(service -> service.loginResp(loginResp));
    }

    public void loadFromDb() {
        map.values().forEach(GameService::load);
        init();
    }

    public void login(Login.PbLoginReq loginMsg, Channel channel, boolean isReconnect) {
        this.channel = channel;
        if (!isReconnect) {
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
        channel = null;
        if (destroyFuture == null) {
            destroyFuture = ThreadPool.getScheduledExecutor().schedule(new DestroyTask(), DESTROY_TIME, TimeUnit.SECONDS);
        }
    }

    public void destroy() {
        log.info("玩家{}销毁", getId());
        saveFuture.cancel(true);
        asyncSave(true);
        dbExecutor.execute(() -> {
            Long delete = getRedisTemplate().opsForHash().delete(RedisKeys.PLAYER_INFO, String.valueOf(getId()));
            if (delete == 0) {
                log.error("删除玩家信息失败！ redis中没有该玩家服务器信息, playerId:{}", getId());
            }
        });
        executor.shutdown();
        destroyFuture.cancel(true);
        destroyFuture = null;
    }

    private StringRedisTemplate getRedisTemplate() {
        return getService(PlayerService.class).getRedisTemplate();
    }

    public boolean awaitAllTaskComplete(long timeout, TimeUnit unit) throws InterruptedException {
        return executor.awaitTermination(timeout, unit);
    }

    public class DestroyTask implements Runnable {

        @Override
        public void run() {
            execute(() -> {
                if (isOnline()) {
                    destroyFuture = null;
                    return;
                }
                if (System.currentTimeMillis() - logoutTime < DESTROY_TIME * 1000) {
                    destroyFuture = ThreadPool.getScheduledExecutor().schedule(this, System.currentTimeMillis() - logoutTime, TimeUnit.MILLISECONDS);
                    return;
                }
                Player.this.destroy();
                Players.removePlayer(getId());
            });
        }
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable action, long initialDelay, long period, TimeUnit unit) {
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
        if (dailyResetDate.isAfter(now)) {
            log.warn("重置每日数据失败，当前日期早于重置日期, 重置日期:{}, 当前日期:{}, 请检查是否存在时钟回拨", dailyResetDate, now);
            return;
        }
        playerInfo.setDailyResetDate(now);
        dailyResetServices.forEach(dailyReset -> dailyReset.reset(dailyResetDate, isSend));
    }

    public void execute(Runnable action) {
        executor.execute(() -> {
            long start = System.nanoTime();
            try {
                action.run();
            } catch (Exception e) {
                log.error("execute action error", e);
            } finally {
                long cost = (System.nanoTime() - start) / TimeUtil.MILLIS_TO_NANOS;
                if (cost > 1000) {
                    log.error("execute action cost {}ms action: {}", cost, action.getClass().getName());
                } else if (cost > 200) {
                    log.warn("execute action cost {}ms action: {}", cost, action.getClass().getName());
                } else if (cost > 50) {
                    log.info("execute action cost {}ms action: {}", cost, action.getClass().getName());
                } else if (log.isDebugEnabled()) {
                    log.debug("execute action cost {}ms action: {}", cost, action.getClass().getName());
                }
            }
        });
    }

    public void execute(Runnable action, Protocol protocol, Message data) {
        executor.execute(() -> {
            long start = System.nanoTime();
            try {
                action.run();
            } catch (Exception e) {
                log.error("处理玩家协议错误, 协议: {} 数据: {}", protocol, ProtobufJsonUtil.serializeMessage(data), e);
            } finally {
                long cost = (System.nanoTime() - start) / TimeUtil.MILLIS_TO_NANOS;
                if (cost > 1000) {
                    log.error("处理玩家协议耗时{}ms 协议: {} 数据: {}", cost, protocol, ProtobufJsonUtil.serializeMessage(data));
                } else if (cost > 200) {
                    log.warn("处理玩家协议耗时{}ms 协议: {} 数据: {}", cost, protocol, ProtobufJsonUtil.serializeMessage(data));
                } else if (cost > 50) {
                    log.info("处理玩家协议耗时{}ms 协议: {} 数据: {}", cost, protocol, ProtobufJsonUtil.serializeMessage(data));
                } else if (log.isDebugEnabled()) {
                    log.debug("执行玩家业务耗时{}ms 协议: {}", cost, protocol);
                }
            }
        });
    }

    public boolean isOnline() {
        return logoutTime == 0;
    }

    public boolean isOffline() {
        return !isOnline();
    }

    public void dbExecute(Runnable action) {
        dbExecutor.execute(action);
    }
}
