package org.game.common.event;

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
        Assertions.assertTrue(listener1.progress == 10);
        Assertions.assertFalse(listener2.isFinished);
        Assertions.assertTrue(listener2.progress == 11);
        Assertions.assertTrue(player.getEventListeners(PlayerEventType.LEVEL_UP).listenersSize() == 1);
        player.levelUp();
        Assertions.assertTrue(listener2.isFinished);
        Assertions.assertTrue(listener2.progress == 12);
        Assertions.assertTrue(player.getEventListeners(PlayerEventType.LEVEL_UP).listenersSize() == 0);

        player.rename("John");
        Assertions.assertTrue(listener3.isFinished);
        Assertions.assertTrue(listener3.progress == 1);
    }

    private class TaskListener implements Listener<Integer> {

        private PlayerEventType type;

        private int progress;

        private int target;

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

    private class Player {

        private int level;

        private String name;

        private PublishManager<PlayerEventType> publishManager = new PublishManager<>(PlayerEventType.values());

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
