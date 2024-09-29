package org.game.common.event;

/**
 * 事件监听器接口
 *
 * @author 王叶峰
 */
public interface EventListener<Event> extends java.util.EventListener {

    /**
     * @param event 事件
     */
    void update(Event event, EventListeners<Event> listeners);
}
