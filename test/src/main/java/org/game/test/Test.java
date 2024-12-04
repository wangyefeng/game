package org.game.test;

import org.game.common.util.JsonUtil;
import org.game.test.db.CfgActivityDao;
import org.game.test.db.CfgItem;
import org.game.test.db.CfgItemDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class Test implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Test.class);

    @Autowired
    private CfgActivityDao cfgActivityDao;

    @Autowired
    private CfgItemDao cfgItemDao;

    public static void main(String[] args) {
        SpringApplication.run(Test.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        long time = System.currentTimeMillis();
        cfgActivityDao.findById("activity_10#20220920");
        log.info("查询耗时：{}ms", (System.currentTimeMillis() - time));
        List<CfgItem> all = cfgItemDao.findAll();
        log.info("查询所有配置项：{}", JsonUtil.toJson(all));
    }

}
