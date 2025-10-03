package org.wyf.game;

import org.wyf.game.common.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class LoginApplication extends Server {

    @Autowired
    private ApplicationContext applicationContext;

    static void main(String[] args) {
        SpringApplication application = new SpringApplication(LoginApplication.class);
        application.setRegisterShutdownHook(false);// 关闭Spring-boot停服处理策略
        application.run(args);
    }

    @Override
    protected void start0() throws Exception {
    }

    @Override
    protected void afterStart() throws Exception {

    }

    @Override
    protected void stop() throws Exception {
        SpringApplication.exit(applicationContext);
    }
}
