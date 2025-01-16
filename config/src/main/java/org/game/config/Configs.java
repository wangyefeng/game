package org.game.config;

import org.game.config.service.CfgService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class Configs {

    private static volatile Configs instance;

    private Map<Class<? extends CfgService>, CfgService> map = new HashMap<>();

    public static void load(Collection<CfgService> cfgServices, boolean check) throws ConfigException {
        Configs newConfigs = new Configs();
        for (CfgService cfgService : cfgServices) {
            newConfigs.map.put(cfgService.getClass(), cfgService);
        }
        if (check) {
            for (CfgService cfgService : cfgServices) {
                cfgService.check(newConfigs);
            }
        }
        instance = newConfigs;
    }

    private Configs() {
    }

    public static Configs getInstance() {
        return instance;
    }

    @SuppressWarnings("unchecked")
    public <T extends CfgService> T get(Class<T> clazz) {
        return (T) map.get(clazz);
    }

    private void add(CfgService cfgService) {
        map.put(cfgService.getClass(), cfgService);
    }
}
