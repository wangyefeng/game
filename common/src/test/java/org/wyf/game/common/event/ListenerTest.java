package org.wyf.game.common.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ListenerTest {

    @Test
    public void testApp() {
        Player player = new Player(1);
        TaskListener listener1 = new TaskListener(PlayerEventType.LEVEL_UP, player.level, 10, false);
        TaskListener listener2 = new TaskListener(PlayerEventType.LEVEL_UP, player.level, 12, false);
        TaskListener listener3 = new TaskListener(PlayerEventType.RENAME, 0, 1, false);
        TaskListener[] listeners = {listener1, listener2, listener3};
        for (TaskListener listener : listeners) {
            player.addEventListener(listener.type, listener);
        }
        for (int i = 0; i < 10; i++) {
            player.levelUp();
        }
        Assertions.assertTrue(listener1.isFinished);
        Assertions.assertEquals(10, listener1.progress);
        Assertions.assertFalse(listener2.isFinished);
        Assertions.assertEquals(11, listener2.progress);
        Assertions.assertEquals(1, player.getEventListeners(PlayerEventType.LEVEL_UP).listenersSize());
        player.levelUp();
        Assertions.assertTrue(listener2.isFinished);
        Assertions.assertEquals(12, listener2.progress);
        Assertions.assertEquals(0, player.getEventListeners(PlayerEventType.LEVEL_UP).listenersSize());

        player.rename("John");
        Assertions.assertTrue(listener3.isFinished);
        Assertions.assertEquals(1, listener3.progress);
    }

    private static class TaskListener implements Listener<Integer> {

        private final PlayerEventType type;

        private int progress;

        private final int target;

        private boolean isFinished;

        public TaskListener(PlayerEventType type, int progress, int target, boolean isFinished) {
            this.type = type;
            this.progress = progress;
            this.target = target;
            this.isFinished = isFinished;
        }

        @Override
        public void update(Integer progress, Publisher<Integer> unloadable) {
            this.progress = progress;
            if (progress >= target) {
                isFinished = true;
                unloadable.unload(this);
            }
        }
    }

    private static class Player {

        private int level;

        private String name;

        private final PublishManager<PlayerEventType> publishManager = new PublishManager<>(PlayerEventType.values());

        public Player(int level) {
            this.level = level;
        }

        public void levelUp() {
            level++;
            update(PlayerEventType.LEVEL_UP, level);
        }

        public void rename(String name) {
            update(PlayerEventType.RENAME, 1);
            this.name = name;
        }

        public String getName() {
            return name;
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
            publishManager.addListener(eventType, listener);
        }
    }

    public enum PlayerEventType {

        LEVEL_UP,

        RENAME,

        ;
    }
}
