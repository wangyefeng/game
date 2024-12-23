package org.game.config;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.game.config.data.entity.Cfg;
import org.game.config.data.service.CfgService;
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
import java.util.Set;

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
            boolean success = true;
            for (CfgService cfgService : cfgServices) {
                for (Object cfg : cfgService.getAllCfg()) {
                    Cfg cfg1 = (Cfg) cfg;
                    Set<ConstraintViolation<Cfg>> validates = validator.validate(cfg1);
                    for (ConstraintViolation<Cfg> validate : validates) {
                        log.error("配置表: {}, id: {}, 字段: {}, 错误: {}", cfgService.getCfgName(cfg1), cfg1.getId(), cfgService.getColumnName(cfg1, validate.getPropertyPath().toString()), validate.getMessage());
                        success = false;
                    }
                }
            }
            if (!success) {
                throw new Exception("配置表检测出现错误，详情请看错误日志！！！");
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
