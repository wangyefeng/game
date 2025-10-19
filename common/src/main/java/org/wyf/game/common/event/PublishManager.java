package org.wyf.game.common.event;

import java.util.HashMap;
import java.util.Map;

/**
 * 事件监听管理器
 *
 * @author 王叶峰
 */
public class PublishManager<EventType extends Enum<EventType>> {

    private final Map<EventType, Publisher<?>> publishers;

    public PublishManager(EventType[] eventTypes) {
        publishers = new HashMap<>(eventTypes.length);
        for (EventType eventType : eventTypes) {
            publishers.put(eventType, new Publisher<>());
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Publisher<?>> T getEventListeners(EventType eventType) {
        return (T) publishers.get(eventType);
    }

    public void update(EventType eventType, Object data) {
        Publisher<Object> listenerManager = getEventListeners(eventType);
        listenerManager.update(data);
    }

    @SuppressWarnings("unchecked")
    public void addListener(EventType eventType, Listener<?> listener) {
        Publisher<Object> listenerManager = getEventListeners(eventType);
        listenerManager.addListener((Listener<Object>) listener);
    }

    @SuppressWarnings("unchecked")
    public void unloadListener(EventType eventType, Listener<?> listener) {
        Publisher<Object> publisher = getEventListeners(eventType);
        publisher.unload((Listener<Object>) listener);
    }
}
