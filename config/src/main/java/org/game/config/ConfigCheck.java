package org.game.config;

import org.game.config.data.service.CfgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.context.ApplicationContext;

import java.util.Collection;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {MongoDataAutoConfiguration.class})
public class ConfigCheck implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ConfigCheck.class);

    @Autowired
    private ApplicationContext applicationContext;

    private void initConfig() {
        log.info("开始加载配置表...");
        long start = System.currentTimeMillis();

        log.info("加载配置完成, 花费: {}毫秒", System.currentTimeMillis() - start);
    }

    @Override
    public void run(String... args) throws Exception {
        Collection<CfgService> cfgServices = applicationContext.getBeansOfType(CfgService.class).values();
        Configs.init(cfgServices);
        for (CfgService cfgService : cfgServices) {
            cfgService.check(Configs.getInstance());
        }
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ConfigCheck.class);
        application.run(args);
    }
}
