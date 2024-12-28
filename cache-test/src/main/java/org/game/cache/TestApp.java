package org.game.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.HashMap;

/**
 * @author wangyefeng
 * @date 2024-07-05
 * @description 网关服务器
 */
@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = BaseDao.class)
public class TestApp implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestApp.class);

    @Autowired
    private MyEntityService myEntityService;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(TestApp.class);
        application.setRegisterShutdownHook(false);
        application.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        MyEntity myEntity = myEntityService.findById(1l);
        myEntity.setMap(new HashMap<>());
        myEntity.getMap().put("key1", "value1");
        myEntityService.save(myEntity);
        myEntity.getMap().put("key1", "value2");
        myEntityService.save(myEntity);
        Thread thread = new Thread(() -> {
            myEntity.getMap().put("key1", "value2");
            myEntityService.save(myEntity);
        });
        thread.start();
    }
}
