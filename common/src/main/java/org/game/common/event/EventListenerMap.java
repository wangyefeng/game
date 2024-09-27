package org.game.common.event;

import java.util.HashMap;
import java.util.Map;

/**
 * 事件监听管理器
 *
 * @author 王叶峰
 */
public class EventListenerMap<EventType> {

    private Map<EventType, EventListeners<?>> eventListeners;

    public EventListenerMap(EventType[] eventTypes) {
        eventListeners = new HashMap<>(eventTypes.length);
        for (EventType eventType : eventTypes) {
            eventListeners.put(eventType, new EventListeners<>());
        }
    }

    public <T extends EventListeners<?>> T getEventListeners(EventType eventType) {
        return (T) eventListeners.get(eventType);
    }

    public void update(EventType eventType, Object data) {
        EventListeners<Object> listenerManager = getEventListeners(eventType);
        listenerManager.update(data);
    }

    public void addEventListener(EventType eventType, EventListener<?> listener) {
        EventListeners<Object> listenerManager = getEventListeners(eventType);
        listenerManager.addListener((EventListener<Object>) listener);
    }
}
