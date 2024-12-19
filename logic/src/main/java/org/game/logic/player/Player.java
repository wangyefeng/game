package org.game.logic.player;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.game.common.event.Listener;
import org.game.common.event.PublishManager;
import org.game.common.event.Publisher;
import org.game.config.Config;
import org.game.config.data.entity.CfgItem;
import org.game.config.data.service.CfgItemService;
import org.game.logic.item.Addable;
import org.game.logic.item.Consumable;
import org.game.logic.item.Item;
import org.game.logic.item.ItemType;
import org.game.logic.service.BackpackService;
import org.game.logic.service.GameService;
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
     * 监听器管理者
     */
    private PublishManager<PlayerEventType> publishManager = new PublishManager<>(PlayerEventType.values());

    public Player(int id, Collection<GameService> gameServices, Channel channel) {
        this.id = id;
        this.channel = channel;
        for (GameService gameService : gameServices) {
            gameService.setPlayer(this);
            map.put(gameService.getClass(), gameService);
            if (gameService instanceof Addable addable) {
                addableService.put(addable.getType(), addable);
                if (gameService instanceof Consumable consumable) {
                    consumableService.put(consumable.getType(), consumable);
                }
            }
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
        BackpackService backpackService = getService(BackpackService.class);
        backpackService.initListener();
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

    public void addItems(Item... items) {
        for (Item item : items) {
            addItem(item);
        }
    }

    public void addItems(Collection<Item> items) {
        for (Item item : items) {
            addItem(item);
        }
    }

    public void addItem(Item item) {
        Config config = Config.getInstance();
        ItemType type = getItemType(item, config);
        Addable addable = addableService.get(type);
        if (addable == null) {
            throw new IllegalArgumentException("addable item type not found: " + type);
        }
        addable.add(item);
    }

    public boolean itemsEnough(Item... items) {
        Collection<Item> mergedItems = mergeItems(items);
        return mergedItemsEnough(mergedItems);
    }

    public boolean itemsEnough(Collection<Item> items) {
        Collection<Item> mergedItems = mergeItems(items);
        return mergedItemsEnough(mergedItems);
    }

    private boolean mergedItemsEnough(Collection<Item> mergedItems) {
        for (Item item : mergedItems) {
            if (!itemEnough(item)) {
                return false;
            }
        }
        return true;
    }

    public boolean itemEnough(Item item) {
        Config config = Config.getInstance();
        ItemType type = getItemType(item, config);
        Consumable consumable = consumableService.get(type);
        if (consumable == null) {
            throw new IllegalArgumentException("consumable item type not found: " + type);
        }
        return consumable.enough(item);
    }

    public void consumeItem(Item item) {
        Config config = Config.getInstance();
        ItemType type = getItemType(item, config);
        Consumable consumable = consumableService.get(type);
        if (consumable == null) {
            throw new IllegalArgumentException("consumable item type not found: " + type);
        }
        consumable.consume(item);
    }

    private static ItemType getItemType(Item item, Config config) {
        CfgItem cfgItem = config.get(CfgItemService.class).getCfg(item.id());
        if (cfgItem == null) {
            throw new IllegalArgumentException("item id not found: " + item.id());
        }
        ItemType type = ItemType.getType(cfgItem.getType());
        if (type == null) {
            throw new IllegalArgumentException("item type not found: " + cfgItem.getType());
        }
        return type;
    }

    public void consumeItems(Item... items) {
        for (Item item : items) {
            consumeItem(item);
        }
    }

    public void consumeItems(Collection<Item> items) {
        for (Item item : items) {
            consumeItem(item);
        }
    }

    public Collection<Item> mergeItems(Collection<Item> items) {
        Map<Integer, Item> result = new HashMap<>();
        for (Item item : items) {
            result.putIfAbsent(item.id(), item);
            result.get(item.id()).add(item.num());
        }
        return result.values();
    }

    public Collection<Item> mergeItems(Item... items) {
        Map<Integer, Item> result = new HashMap<>();
        for (Item item : items) {
            result.putIfAbsent(item.id(), item);
            result.get(item.id()).add(item.num());
        }
        return result.values();
    }
}
