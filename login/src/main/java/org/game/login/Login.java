package org.game.login;

import org.game.common.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Login extends Server {

    private static final Logger log = LoggerFactory.getLogger(Login.class);
    @Autowired
    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Login.class);
        application.setRegisterShutdownHook(false);// 关闭Spring-boot停服处理策略
        application.run(args);
    }

    @Override
    protected void start0() throws Exception {
        log.info("login test !!!");
    }

    @Override
    protected void afterStart() throws Exception {

    }

    @Override
    protected void stop() throws Exception {
        SpringApplication.exit(applicationContext);
    }
}
