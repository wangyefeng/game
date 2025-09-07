package org.game.login;

import org.game.common.Server;
import org.game.config.tools.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(basePackages = {"org.game.common", "org.game.config"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Tool.class))
public class Login extends Server {

    @Autowired
    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Login.class);
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
