package org.game.common.event;

import java.util.ArrayList;
import java.util.List;

/**
 * 事件监听管理器
 *
 * @author 王叶峰
 */
public class EventListeners<Event> {

    /**
     * 观察者列表
     */
    private final List<EventListener<Event>> listeners = new ArrayList<>();

    public void addListener(EventListener<Event> listener) {
        listeners.add(listener);
    }

    public void unload(EventListener<Event> listener) {
        listeners.remove(listener);
    }

    public void update(Event event) {
        EventListener<Event>[] arrLocal = listeners.toArray(new EventListener[listeners.size()]);
        for (EventListener<Event> eventListener : arrLocal) {
            eventListener.update(event, this);
        }
    }

    public void clear() {
        listeners.clear();
    }

    public int size() {
        return listeners.size();
    }
}
