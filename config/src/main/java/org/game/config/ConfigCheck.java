package org.game.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConfigCheck {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ConfigCheck.class);
        application.run(args);
    }
}
