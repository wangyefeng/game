package org.game.test;

import org.game.test.db.CfgActivity;
import org.game.test.db.CfgActivityDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Hello world!
 */
@SpringBootApplication
@EnableCaching  // 启用缓存
public class Test implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Test.class);

    @Autowired
    private CfgActivityDao cfgActivityDao;

    public static void main(String[] args) {
        SpringApplication.run(Test.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        long time = System.currentTimeMillis();
        CfgActivity result = cfgActivityDao.findById("activity_10#20220920");
        log.info("MySQL查询耗时：{}ms 结果：{}", (System.currentTimeMillis() - time), result);
    }

}
