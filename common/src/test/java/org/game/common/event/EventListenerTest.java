package org.game.common.event;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

        KillFishListener listener3 = new KillFishListener(0, 5);
        MultipleEventListenerManager<Integer, Integer> KillFishListeners = player.getMultipleEventListeners(EventType.KILL_FISH);
        player.addMultipleEventListenerManager(EventType.KILL_FISH, Set.of(1, 2, 3), listener3);
        player.killFish(1);
        player.killFish(2);
        player.killFish(3);
        assertFalse(listener3.isFinished);
        assertTrue(listener3.current == 3);

        player.killFish(2);
        player.killFish(3);
        assertTrue(listener3.isFinished);
        assertTrue(listener3.current == 5);
        assertTrue(KillFishListeners.size() == 0);
        player.killFish(3);
        assertTrue(listener3.isFinished);
        assertTrue(listener3.current == 5);
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
        public void update(Integer level, Unloadable<Integer> unloadable) {
            currentLevel = level;
            if (level >= targetLevel) {
                isFinished = true;
                unloadable.unload(this);
            }
        }
    }

    private class KillFishListener implements EventListener<Integer> {

        private int current;

        private int target;

        private boolean isFinished;

        public KillFishListener(int current, int target) {
            this.current = current;
            this.target = target;
            this.isFinished = current >= target;
        }

        @Override
        public void update(Integer id, Unloadable<Integer> unloadable) {
            current++;
            if (current >= target) {
                isFinished = true;
                unloadable.unload(this);
            }
        }
    }

    private class Player {

        private int level;

        private Map<EventType, EventListenerManager<?>> eventListeners = new HashMap<>();

        private Map<EventType, MultipleEventListenerManager<?, ?>> multipleEventListeners = new HashMap<>();

        public Player(int level) {
            this.level = level;
            for (EventType eventType : EventType.values()) {
                if (eventType.multiple) {
                    multipleEventListeners.put(eventType, new MultipleEventListenerManager<>());
                } else {
                    eventListeners.put(eventType, new EventListenerManager<>());
                }
            }
        }

        public void levelUp() {
            level++;
            update(EventType.LEVEL_UP, level);
        }

        public void killFish(int id) {
            update(EventType.KILL_FISH, id, 1);
        }

        public <T extends EventListenerManager<?>> T getEventListeners(EventType eventType) {
            return (T) eventListeners.get(eventType);
        }

        public <T extends MultipleEventListenerManager<?, ?>> T getMultipleEventListeners(EventType eventType) {
            return (T) multipleEventListeners.get(eventType);
        }

        public void update(EventType eventType, Object data) {
            EventListenerManager<Object> listenerManager = getEventListeners(eventType);
            listenerManager.update(data);
        }

        public void update(EventType eventType, Object condition, Object data) {
            MultipleEventListenerManager<Object, Object> multipleEventListenerManager = getMultipleEventListeners(eventType);
            multipleEventListenerManager.update(condition, data);
        }

        public void addEventListener(EventType eventType, EventListener<?> listener) {
            EventListenerManager<Object> listenerManager = getEventListeners(eventType);
            listenerManager.addListener((EventListener<Object>) listener);
        }

        public void addMultipleEventListenerManager(EventType eventType, Set<?> conditions, EventListener<?> listener) {
            MultipleEventListenerManager<Object, Object> multipleEventListenerManager = getMultipleEventListeners(eventType);
            multipleEventListenerManager.addConditions((Set<Object>) conditions, (EventListener<Object>) listener);
        }
    }

    public enum EventType {

        LEVEL_UP,

        KILL_FISH(true),
        ;

        private final boolean multiple;

        EventType() {
            this(false);
        }

        EventType(boolean multiple) {
            this.multiple = multiple;
        }
    }
}
