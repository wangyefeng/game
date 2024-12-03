package org.game.spring.cache;

import org.game.spring.cache.db.CfgActivity;
import org.game.spring.cache.db.CfgActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.util.List;

@SpringBootApplication
@EnableCaching
public class SpringCacheApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringCacheApplication.class);

    @Autowired
    private CfgActivityService cfgActivityService;

    public static void main(String[] args) {
        SpringApplication.run(SpringCacheApplication.class, args);
    }

    @Override
    public void run(String... args) {
        long t1 = System.currentTimeMillis();
        List<CfgActivity> all = cfgActivityService.findAll();
        log.info("加载配置信息耗时：{}ms", System.currentTimeMillis() - t1);
        t1 = System.currentTimeMillis();
        cfgActivityService.findAll();
        log.info("第二次加载配置信息耗时：{}ms", System.currentTimeMillis() - t1);
    }

}
