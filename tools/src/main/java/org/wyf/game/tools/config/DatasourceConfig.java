package org.wyf.game.tools.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "config.datasource")
public record DatasourceConfig(String jdbcUrl, String username, String password) {
}
