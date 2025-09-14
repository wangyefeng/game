package org.game.gate.net.client;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 客户端组
 */
@Component
public class ClientGroup<C extends Client> {

    // accessOrder = true 表示按访问顺序排列，实现轮询算法
    private final LinkedHashMap<String, C> clients = new LinkedHashMap<>(16, 0.75f, true);

    public ClientGroup() {
    }

    public synchronized C remove(String id) {
        return clients.remove(id);
    }

    public synchronized void add(C client) {
        clients.put(client.getId(), client);
    }

    public synchronized void close() {
        for (Client client : clients.values()) {
            try {
                client.close();
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
    }

    public synchronized C get(String id) {
        return clients.get(id);
    }

    /**
     * 轮询客户端
     *
     * @return 客户端
     */
    public synchronized C next() {
        return clients.get(clients.firstEntry().getKey());
    }

    public synchronized boolean contains(String id) {
        return clients.containsKey(id);
    }

    public synchronized Map<String, C> getClients() {
        return clients;
    }

}
