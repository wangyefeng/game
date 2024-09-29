package org.game.common.event;

import java.util.ArrayList;
import java.util.List;

/**
 * 事件发布者
 *
 * @author 王叶峰
 */
public class Publisher<Event> {

    /**
     * 观察者列表
     */
    private final List<Listener<Event>> listeners = new ArrayList<>();

    public void addListener(Listener<Event> listener) {
        listeners.add(listener);
    }

    public void unload(Listener<Event> listener) {
        listeners.remove(listener);
    }

    public void update(Event event) {
        Listener<Event>[] arrLocal = listeners.toArray(new Listener[listeners.size()]);
        for (Listener<Event> listener : arrLocal) {
            listener.update(event, this);
        }
    }

    public void clear() {
        listeners.clear();
    }

    public int size() {
        return listeners.size();
    }
}
