package org.game.config;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@EntityScan(basePackages = "org.game.config.entity")
@EnableJpaRepositories({"org.game.config.repository"})
public class Config implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(Config.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DatabaseService databaseService;

    @Value("${config.check:false}")
    private boolean checkConfig;

    private void initConfig() throws Exception {
        databaseService.lockDatabase();
        try {
            log.info("开始加载配置表...");
            long start = System.currentTimeMillis();
            Collection<CfgService> cfgServices = applicationContext.getBeansOfType(CfgService.class).values();
            Configs.init(cfgServices);
            log.info("加载配置表完成, 耗时: {}毫秒", System.currentTimeMillis() - start);
            if (checkConfig) {
                check(cfgServices);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            databaseService.unlockDatabase();
        }
    }

    private void check(Collection<CfgService> cfgServices) throws Exception {
        log.info("开始检查配置表...");
        List<ConfigException> configExceptions = new ArrayList<>();
        for (CfgService cfgService : cfgServices) {
            try {
                cfgService.check(Configs.getInstance());
            } catch (ConfigException e) {
                configExceptions.add(e);
            }
        }
        if (!configExceptions.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConfigException e : configExceptions) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append("表：");
                sb.append(e.getTableName());
                sb.append(" id: ");
                sb.append(e.getId());
                sb.append(" 字段: ");
                sb.append(e.getFieldName());
                sb.append(" 错误信息: ");
                sb.append(e.getMessage());
            }
            throw new Exception("配置表校验失败!!! 错误信息如下:\n" + sb);
        }
        log.info("检查配置表完成。");
    }

    public void reloadAllConfig() {
        databaseService.lockDatabase();
        try {
            long start = System.currentTimeMillis();
            log.info("开始重新加载配置表...");
            try {
                Configs.reload(applicationContext.getBeansOfType(CfgService.class).values());
            } catch (Exception e) {
                log.error("重载配置表失败", e);
                return;
            }
            log.info("配置表重新加载完成, 耗时: {}毫秒", System.currentTimeMillis() - start);
        } catch (Exception e) {
            throw e;
        } finally {
            databaseService.unlockDatabase();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initConfig();
    }
}
