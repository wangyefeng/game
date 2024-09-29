package org.game.common.event;

import java.util.HashMap;
import java.util.Map;

/**
 * 事件监听管理器
 *
 * @author 王叶峰
 */
public class PublishManager<EventType extends Enum<EventType>> {

    private Map<EventType, Publisher<?>> publishers;

    public PublishManager(EventType[] eventTypes) {
        publishers = new HashMap<>(eventTypes.length);
        for (EventType eventType : eventTypes) {
            publishers.put(eventType, new Publisher<>());
        }
    }

    public <T extends Publisher<?>> T getEventListeners(EventType eventType) {
        return (T) publishers.get(eventType);
    }

    public void update(EventType eventType, Object data) {
        Publisher<Object> listenerManager = getEventListeners(eventType);
        listenerManager.update(data);
    }

    public void addEventListener(EventType eventType, Listener<?> listener) {
        Publisher<Object> listenerManager = getEventListeners(eventType);
        listenerManager.addListener((Listener<Object>) listener);
    }
}
