package org.game.logic.data.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "org.game.logic.data.config", mongoTemplateRef = "configMongoTemplate")
public class ConfigMongoConfig {
}