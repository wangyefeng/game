package org.game.common.event;

import junit.framework.TestCase;

public class ListenerTest extends TestCase {

    public void testApp() {
        Player player = new Player(1);
        LevelUpListener listener1 = new LevelUpListener(player.level, 10);
        LevelUpListener listener2 = new LevelUpListener(player.level, 12);
        player.addEventListener(PlayerEventType.LEVEL_UP, listener1);
        player.addEventListener(PlayerEventType.LEVEL_UP, listener2);
        for (int i = 0; i < 10; i++) {
            player.levelUp();
        }
        assertTrue(listener1.isFinished);
        assertTrue(listener1.currentLevel == 10);
        assertFalse(listener2.isFinished);
        assertTrue(listener2.currentLevel == 11);
        assertTrue(player.getEventListeners(PlayerEventType.LEVEL_UP).size() == 1);
        player.levelUp();
        assertTrue(listener2.isFinished);
        assertTrue(listener2.currentLevel == 12);
        assertTrue(player.getEventListeners(PlayerEventType.LEVEL_UP).size() == 0);
    }

    private class LevelUpListener implements Listener<Integer> {

        private int currentLevel;

        private int targetLevel;

        private boolean isFinished;

        public LevelUpListener(int currentLevel, int targetLevel) {
            this.currentLevel = currentLevel;
            this.targetLevel = targetLevel;
            isFinished = currentLevel >= targetLevel;
        }

        @Override
        public void update(Integer level, Publisher<Integer> unloadable) {
            currentLevel = level;
            if (level >= targetLevel) {
                isFinished = true;
                unloadable.unload(this);
            }
        }
    }

    private class Player {

        private int level;

        private PublishManager<PlayerEventType> publishManager = new PublishManager<>(PlayerEventType.values());

        public Player(int level) {
            this.level = level;
        }

        public void levelUp() {
            level++;
            update(PlayerEventType.LEVEL_UP, level);
        }

        public PublishManager<PlayerEventType> getEventListeners() {
            return publishManager;
        }

        public void update(PlayerEventType eventType, Object value) {
            publishManager.update(eventType, value);
        }

        public <T extends Publisher<?>> T getEventListeners(PlayerEventType eventType) {
            return publishManager.getEventListeners(eventType);
        }

        public void addEventListener(PlayerEventType eventType, Listener<?> listener) {
            publishManager.addEventListener(eventType, listener);
        }
    }

    public enum PlayerEventType {

        LEVEL_UP,

        ;
    }
}
