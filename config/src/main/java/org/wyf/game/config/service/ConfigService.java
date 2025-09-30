package org.wyf.game.config.service;

import org.wyf.game.common.event.Listener;
import org.wyf.game.common.event.Publisher;
import org.wyf.game.config.ConfigException;
import org.wyf.game.config.Configs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ConfigService {

    private static final Logger log = LoggerFactory.getLogger(ConfigService.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DatabaseService databaseService;

    @Value("${config.check:true}")
    private boolean checkConfig;

    private final Publisher<Object> reloadPublishers = new Publisher<>();

    public void initConfig() throws ConfigException {
        databaseService.lockDatabase();
        try {
            log.info("开始加载配置表...");
            long start = System.currentTimeMillis();
            Collection<CfgService> cfgServices = applicationContext.getBeansOfType(CfgService.class).values();
            Configs.reload(cfgServices, checkConfig);
            log.info("加载配置表完成, 耗时: {}毫秒", System.currentTimeMillis() - start);
        } finally {
            databaseService.unlockDatabase();
        }
    }

    public synchronized void reload() throws ConfigException {
        initConfig();
        reloadPublishers.update(null);
    }

    public void addReloadPublisher(Listener<Object> listener) {
        reloadPublishers.addListener(listener);
    }
}
