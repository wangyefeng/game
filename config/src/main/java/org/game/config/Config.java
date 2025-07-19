package org.game.config;

import org.game.common.event.Listener;
import org.game.common.event.Publisher;
import org.game.config.service.CfgService;
import org.game.config.service.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@EntityScan(basePackages = "org.game.config.entity")
@EnableJpaRepositories({"org.game.config.repository"})
public class Config implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(Config.class);

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

    @Override
    public void afterPropertiesSet() throws Exception {
        initConfig();
    }

    public void addReloadPublisher(Listener<Object> listener) {
        reloadPublishers.addListener(listener);
    }
}
