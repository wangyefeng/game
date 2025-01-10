package org.game.logic.player;

import com.google.protobuf.Message;
import io.netty.channel.Channel;
import org.game.common.event.Listener;
import org.game.common.event.PublishManager;
import org.game.common.event.Publisher;
import org.game.config.Configs;
import org.game.config.entity.CfgItem;
import org.game.config.entity.Item;
import org.game.config.service.CfgItemService;
import org.game.logic.GameService;
import org.game.logic.player.item.Addable;
import org.game.logic.player.item.AddableItem;
import org.game.logic.player.item.Consumable;
import org.game.logic.player.item.ItemType;
import org.game.logic.thread.ThreadPool;
import org.game.proto.MessagePlayer;
import org.game.proto.protocol.LogicToClientProtocol;
import org.game.proto.protocol.LogicToGateProtocol;
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

    public void writeToClient(LogicToClientProtocol protocol, Message message) {
        channel.writeAndFlush(new MessagePlayer<>(getId(), protocol, message));
    }

    public void writeToClient(LogicToClientProtocol protocol) {
        writeToClient(protocol, null);
    }

    public void writeToGate(LogicToGateProtocol protocol, Message message) {
        channel.writeAndFlush(new MessagePlayer<>(getId(), protocol, message));
    }

    public void writeToGate(LogicToGateProtocol protocol) {
        writeToGate(protocol, null);
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

    public void register(Login.PbRegisterReq registerMsg) {
        map.values().forEach(service -> service.register(registerMsg));
        init();
    }

    private void startSaveTimer() {
        saveFuture = scheduleAtFixedRate(this::asyncSave, 0, 1, TimeUnit.MINUTES);
    }

    public void loginResp(Login.PbLoginResp.Builder loginResp) {
        map.values().forEach(service -> service.loginResp(loginResp));
    }

    public void login(Login.PbLoginReq loginMsg) {
        map.values().forEach(GameService::load);
        init();
    }

    private void init() {
        map.values().forEach(GameService::init);
        startSaveTimer();
    }

    public void logout() {
        saveFuture.cancel(true);
        asyncSave();
        channel = null;
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

    public <T extends Item> void addItems(T... items) {
        for (Item item : items) {
            addItem(item);
        }
    }

    public <T extends Item> void addItems(Collection<T> items) {
        for (Item item : items) {
            addItem(item);
        }
    }

    public void addItem(Item item) {
        Configs config = Configs.getInstance();
        ItemType type = getItemType(item, config);
        Addable addable = addableService.get(type);
        if (addable == null) {
            throw new IllegalArgumentException("addable item type not found: " + type);
        }
        addable.add(item);
    }

    public <T extends Item> boolean itemsEnough(T... items) {
        Collection<AddableItem> mergedItems = mergeItems(items);
        return mergedItemsEnough(mergedItems);
    }

    public <T extends Item> boolean itemsEnough(Collection<T> items) {
        Collection<AddableItem> mergedItems = mergeItems(items);
        return mergedItemsEnough(mergedItems);
    }

    private <T extends Item> boolean mergedItemsEnough(Collection<T> mergedItems) {
        for (Item item : mergedItems) {
            if (!itemEnough(item)) {
                return false;
            }
        }
        return true;
    }

    public boolean itemEnough(Item item) {
        Configs config = Configs.getInstance();
        ItemType type = getItemType(item, config);
        Consumable consumable = consumableService.get(type);
        if (consumable == null) {
            throw new IllegalArgumentException("consumable item type not found: " + type);
        }
        return consumable.enough(item);
    }

    public void consumeItem(Item item) {
        Configs config = Configs.getInstance();
        ItemType type = getItemType(item, config);
        Consumable consumable = consumableService.get(type);
        if (consumable == null) {
            throw new IllegalArgumentException("consumable item type not found: " + type);
        }
        consumable.consume(item);
    }

    private static ItemType getItemType(Item item, Configs config) {
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

    public <T extends Item> Collection<AddableItem> mergeItems(Collection<T> items) {
        Map<Integer, AddableItem> result = new HashMap<>();
        for (Item item : items) {
            result.computeIfAbsent(item.id(), _ -> new AddableItem(item.id(), item.num()));
            result.get(item.id()).add(item.num());
        }
        return result.values();
    }

    public <T extends Item> Collection<AddableItem> mergeItems(T... items) {
        Map<Integer, AddableItem> result = new HashMap<>();
        for (Item item : items) {
            result.computeIfAbsent(item.id(), _ -> new AddableItem(item.id(), item.num()));
            result.get(item.id()).add(item.num());
        }
        return result.values();
    }
}
