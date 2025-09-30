package org.wyf.game.config;


import org.wyf.game.config.service.CfgService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class Configs {

    private static volatile Configs instance;

    private final Map<Class<? extends CfgService>, CfgService> map = new HashMap<>();

    public static void reload(Collection<CfgService> cfgServices, boolean check) throws ConfigException {
        for (CfgService cfgService : cfgServices) {
            try {
                cfgService.init();
            } catch (Exception e) {
                throw new ConfigException(cfgService.getTableName(), e.getMessage());
            }
        }
        Configs newConfigs = new Configs();
        cfgServices.forEach(cfgService -> newConfigs.map.put(cfgService.getClass(), cfgService));
        if (check) {
            for (CfgService cfgService : cfgServices) {
                cfgService.validate(newConfigs);
            }
        }
        instance = newConfigs;
    }

    private Configs() {
    }

    public static <T extends CfgService<?, ?, ?>> T of(Class<T> clazz) {
        return instance.getCfgService(clazz);
    }

    public  <T extends CfgService<?, ?, ?>> T getCfgService(Class<T> clazz) {
        return (T) map.get(clazz);
    }
}
