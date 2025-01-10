package org.game.config.tools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(basePackages = {"org.game.config"}, excludeFilters = @Filter(type = FilterType.ANNOTATION, value = Tool.class))
public class Check {

    public static void main(String[] args) {
        SpringApplication.run(Check.class, args);
    }
}
