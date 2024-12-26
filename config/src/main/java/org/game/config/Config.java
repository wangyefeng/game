package org.game.config;

import jakarta.persistence.EntityManager;
import jakarta.validation.Validator;
import org.game.config.service.CfgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
@EnableAutoConfiguration(exclude = {MongoDataAutoConfiguration.class})
public class Config implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(Config.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${config.check:false}")
    private boolean checkConfig;

    @Autowired
    protected Validator validator;

    @Autowired
    protected EntityManager entityManager;

    private void initConfig() throws Exception {
        log.info("开始加载配置表...");
        long start = System.currentTimeMillis();
        Collection<CfgService> cfgServices = applicationContext.getBeansOfType(CfgService.class).values();
        Configs.init(cfgServices);
        if (checkConfig) {
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
        }
        log.info("加载配置完成, 花费: {}毫秒", System.currentTimeMillis() - start);
    }

    public void reloadConfig(String... tableNames) {
        Collection<CfgService> cfgServices = new ArrayList<>();
        for (String tableName : tableNames) {
            CfgService cfgService = applicationContext.getBean(tableName, CfgService.class);
            if (cfgService == null) {
                throw new IllegalArgumentException("重载配置表失败, 未找到配置表: " + tableName);
            }
            cfgServices.add(cfgService);
        }
        try {
            Configs.reload(cfgServices);
        } catch (Exception e) {
            log.error("重载配置表: {}", Arrays.toString(tableNames), e);
            return;
        }
        log.info("重载配置表: {}", Arrays.toString(tableNames));
    }

    public void reloadAllConfig() {
        long start = System.currentTimeMillis();
        log.info("开始重新加载配置表...");
        try {
            Configs.reload(applicationContext.getBeansOfType(CfgService.class).values());
        } catch (Exception e) {
            log.error("重载配置表失败", e);
            return;
        }
        log.info("配置表重新加载完成, 耗时: {}毫秒", System.currentTimeMillis() - start);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initConfig();
    }
}
