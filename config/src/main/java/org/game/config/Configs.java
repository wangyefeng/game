package org.game.config;

import org.game.config.service.CfgService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class Configs {

    private static volatile Configs instance;

    private Map<Class<? extends CfgService>, CfgService> map = new HashMap<>();

    public static void init(Collection<CfgService> cfgServices) {
        Configs.instance = new Configs();
        cfgServices.forEach(Configs.instance::add);
    }

    public static void reload(Collection<CfgService> cfgServices) throws ConfigException {
        Map<Class<? extends CfgService>, CfgService> newMap = new HashMap<>(instance.map);
        for (CfgService cfgService : cfgServices) {
            newMap.put(cfgService.getClass(), cfgService);
        }
        for (CfgService cfgService : cfgServices) {
            cfgService.check(instance);
        }
        instance.map = newMap;
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
