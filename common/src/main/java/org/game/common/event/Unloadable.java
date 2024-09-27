package org.game.common.event;

public interface Unloadable<Event> {

    void unload(EventListener<Event> event);

    void clear();
}
