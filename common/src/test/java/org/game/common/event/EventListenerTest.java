package org.game.common.event;

import junit.framework.TestCase;

public class EventListenerTest extends TestCase {

    public void testApp() {
        Player player = new Player(1);
        LevelUpListener listener1 = new LevelUpListener(player.level, 10);
        LevelUpListener listener2 = new LevelUpListener(player.level, 12);
        player.addEventListener(EventType.LEVEL_UP, listener1);
        player.addEventListener(EventType.LEVEL_UP, listener2);
        for (int i = 0; i < 10; i++) {
            player.levelUp();
        }
        assertTrue(listener1.isFinished);
        assertTrue(listener1.currentLevel == 10);
        assertFalse(listener2.isFinished);
        assertTrue(listener2.currentLevel == 11);
        assertTrue(player.getEventListeners(EventType.LEVEL_UP).size() == 1);
        player.levelUp();
        assertTrue(listener2.isFinished);
        assertTrue(listener2.currentLevel == 12);
        assertTrue(player.getEventListeners(EventType.LEVEL_UP).size() == 0);
    }

    private class LevelUpListener implements EventListener<Integer> {

        private int currentLevel;

        private int targetLevel;

        private boolean isFinished;

        public LevelUpListener(int currentLevel, int targetLevel) {
            this.currentLevel = currentLevel;
            this.targetLevel = targetLevel;
            isFinished = currentLevel >= targetLevel;
        }

        @Override
        public void update(Integer level, EventListeners<Integer> unloadable) {
            currentLevel = level;
            if (level >= targetLevel) {
                isFinished = true;
                unloadable.unload(this);
            }
        }
    }

    private class Player {

        private int level;

        private EventListenerMap<EventType> eventListenerMap = new EventListenerMap<>(EventType.values());

        public Player(int level) {
            this.level = level;
        }

        public void levelUp() {
            level++;
            update(EventType.LEVEL_UP, level);
        }

        public EventListenerMap<EventType> getEventListeners() {
            return eventListenerMap;
        }

        public void update(EventType type, Object value) {
            eventListenerMap.update(type, value);
        }

        public <T extends EventListeners<?>> T getEventListeners(EventType eventType) {
            return eventListenerMap.getEventListeners(eventType);
        }

        public void addEventListener(EventType eventType, EventListener<?> listener) {
            eventListenerMap.addEventListener(eventType, listener);
        }
    }

    public enum EventType {

        LEVEL_UP,

        ;
    }
}
