package org.game.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author wangyefeng
 * @date 2024-07-05
 * @description 网关服务器
 */
@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = BaseDao.class)
public class TestApp implements CommandLineRunner {

    @Autowired
    private MyEntityService myEntityService;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(TestApp.class);
        application.setRegisterShutdownHook(false);
        application.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        MyEntity myEntity = myEntityService.findById(1L);
        myEntity.getMap().clear();
        myEntityService.save(myEntity);
        myEntity.getMap().put("k", "v");
        myEntityService.save(myEntity);
        new Thread(() -> {
            myEntity.getMap().put("k", "v");
            myEntityService.save(myEntity);
        }).start();
    }
}
