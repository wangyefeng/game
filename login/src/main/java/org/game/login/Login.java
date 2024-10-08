package org.game.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Login {

    private static final Logger log = LoggerFactory.getLogger(Login.class);

    static {
        Thread.setDefaultUncaughtExceptionHandler((_, e) -> log.error("未捕获异常！", e));
    }

    public static void main(String[] args) {
        SpringApplication.run(Login.class, args);
    }
}
