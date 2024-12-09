package org.game.logic.data.config;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "org.game.logic.data.config.repository", mongoTemplateRef = "configMongoTemplate")
public class ConfigDataSource {

    @Bean
    @ConfigurationProperties(prefix = "spring.data.mongodb.config")
    public MongoProperties configMongoProperties() {
        return new MongoProperties();
    }

    @Bean
    public MongoTemplate configMongoTemplate() {
        MongoProperties mongoProperties = configMongoProperties();
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(mongoProperties.getUri()));
    }

}