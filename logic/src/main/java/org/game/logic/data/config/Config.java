package org.game.logic.data.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class Config {

    private static volatile Config instance;

    private Map<Class<? extends CfgService>, CfgService> map = new HashMap<>();

    public static void reload(Collection<CfgService> cfgServices) {
        Config config = new Config();
        cfgServices.forEach(config::add);
        Config.instance = config;
    }

    private Config() {
    }

    public static Config getInstance() {
        return instance;
    }

    public <T extends CfgService> T get(Class<T> clazz) {
        return (T) map.get(clazz);
    }

    private void add(CfgService cfgService) {
        map.put(cfgService.getClass(), cfgService);
    }
}
