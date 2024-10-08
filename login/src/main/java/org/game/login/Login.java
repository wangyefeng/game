package org.game.login;

import org.apache.logging.log4j.core.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Login {

    private static final Logger log = LoggerFactory.getLogger(Login.class);

    static {
        Thread.setDefaultUncaughtExceptionHandler((_, e) -> log.error("未捕获异常！", e));
        System.setProperty(Constants.LOG4J_CONTEXT_SELECTOR, "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
    }

    public static void main(String[] args) {
        SpringApplication.run(Login.class, args);
        log.info("登录服务启动成功！");
    }
}
