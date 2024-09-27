package org.game.common.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 多条件列表观察期
 *
 * @Author: 王叶峰
 */
public class MultipleEventListenerManager<Condition, Event> implements Unloadable<Event> {

    private Map<Condition, Set<EventListener<Event>>> subscriberMap = new HashMap<>();

    private Map<EventListener, Set<Condition>> conditionMap = new HashMap<>();

    public MultipleEventListenerManager() {
    }

    public void addConditions(Set<Condition> conditions, EventListener<Event> eventListener) {
        conditionMap.put(eventListener, conditions);
        conditions.forEach(condition -> {
            subscriberMap.putIfAbsent(condition, new HashSet<>());
            subscriberMap.get(condition).add(eventListener);
        });
    }

    public void addCondition(Condition condition, EventListener<Event> eventListener) {
        Set<Condition> set = new HashSet<>();
        set.add(condition);
        addConditions(set, eventListener);
    }

    public void update(Condition condition, Event event) {
        Set<EventListener<Event>> listeners = subscriberMap.get(condition);
        if (listeners == null) {
            return;
        }
        EventListener<Event>[] arrSub = listeners.toArray(new EventListener[listeners.size()]);
        for (EventListener<Event> eventListener : arrSub) {
            eventListener.update(event, this);
        }
    }

    public void unload(EventListener<Event> listener) {
        Set<Condition> conditions = conditionMap.remove(listener);
        if (conditions != null) {
            conditions.forEach(condition -> {
                Set<EventListener<Event>> eventListenerSet = subscriberMap.get(condition);
                eventListenerSet.remove(listener);
                if (eventListenerSet.isEmpty()) {
                    subscriberMap.remove(condition);
                }
            });
        }
    }

    public void clear() {
        subscriberMap.clear();
        conditionMap.clear();
    }

    public int size() {
        return subscriberMap.size();
    }
}
