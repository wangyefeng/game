package org.game.gate.net.client;

import org.game.common.random.RandomUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端组
 *
 * @author 王叶峰
 */
@Component
public class ClientGroup<C extends Client> {

    private final Map<Integer, C> clients = new HashMap<>();

    public ClientGroup() {
    }

    public void remove(Integer id) {
        clients.remove(id);
    }

    public void add(C client) {
        clients.put(client.getId(), client);
    }

    public void close() {
        for (Client client : clients.values()) {
            try {
                client.close();
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
    }

    public C get(Integer id) {
        return clients.get(id);
    }

    public C next() {
        return RandomUtil.random(clients.values());
    }

    public boolean contains(int id) {
        return clients.containsKey(id);
    }

    public Map<Integer, C> getClients() {
        return clients;
    }
}
