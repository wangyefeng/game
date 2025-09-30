package org.wyf.game.common.event;

import java.util.HashSet;
import java.util.Set;

/**
 * 事件发布者
 *
 * @author 王叶峰
 */
public class Publisher<Event> {

    /**
     * 观察者列表
     */
    private final Set<Listener<Event>> listeners = new HashSet<>();

    public void addListener(Listener<Event> listener) {
        listeners.add(listener);
    }

    public void unload(Listener<Event> listener) {
        listeners.remove(listener);
    }

    @SuppressWarnings("unchecked")
    public void update(Event event) {
        Listener<Event>[] arrLocal = listeners.toArray(new Listener[0]);
        for (Listener<Event> listener : arrLocal) {
            listener.update(event, this);
        }
    }

    public void clearListeners() {
        listeners.clear();
    }

    public int listenersSize() {
        return listeners.size();
    }

    public boolean listenersEmpty() {
        return listeners.isEmpty();
    }
}
