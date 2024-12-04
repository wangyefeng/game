package org.game.logic.data.mongodb.game;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "org.game.logic.data.mongodb.game", mongoTemplateRef = "gameMongoTemplate")
public class GameMongoConfig {
}