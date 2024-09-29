package org.game.common.event;

/**
 * 事件监听器
 *
 * @author 王叶峰
 */
public interface Listener<Event> extends java.util.EventListener {

    /**
     * @param event 事件
     */
    void update(Event event, Publisher<Event> listeners);
}
