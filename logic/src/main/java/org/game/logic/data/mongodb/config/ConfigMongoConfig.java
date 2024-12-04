package org.game.logic.data.mongodb.config;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "org.game.logic.data.mongodb.config", mongoTemplateRef = "configMongoTemplate")
public class ConfigMongoConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.data.mongodb.config")
    public MongoProperties configMongoProperties() {
        return new MongoProperties();
    }

    @Bean
    public MongoTemplate configMongoTemplate() {
        MongoProperties mongoProperties = configMongoProperties();
        MongoDatabaseFactory mongoDatabaseFactory = new SimpleMongoClientDatabaseFactory(mongoProperties.getUri());
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDatabaseFactory);
        MappingMongoConverter mongoMapping = (MappingMongoConverter) mongoTemplate.getConverter();
        mongoMapping.afterPropertiesSet();
        return mongoTemplate;
    }

}