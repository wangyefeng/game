package org.game.config;

import org.game.config.tools.MysqlToExcel;
import org.game.config.tools.XlsxToSql;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
        basePackages = "org.game.config",  // 扫描该包及其子包
        excludeFilters = {@Filter(type = FilterType.ASSIGNABLE_TYPE, value = XlsxToSql.class), @Filter(type = FilterType.ASSIGNABLE_TYPE, value = MysqlToExcel.class)}
)
public class ConfigCheck {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ConfigCheck.class);
        application.run(args);
    }
}
