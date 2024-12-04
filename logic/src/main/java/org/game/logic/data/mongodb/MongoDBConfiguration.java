package org.game.logic.data.mongodb;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

@Configuration
public class MongoDBConfiguration {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.data.mongodb.game")
    public MongoProperties gameMongoProperties() {
        return new MongoProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.data.mongodb.config")
    public MongoProperties configMongoProperties() {
        return new MongoProperties();
    }

    /**
     * 默认 MongoTemplate
     *
     * @return
     */
    @Bean
    @Primary
    public MongoTemplate gameMongoTemplate() {
        MongoTemplate mongoTemplate = new MongoTemplate(gameMongoFactory(gameMongoProperties()));
        MappingMongoConverter mongoMapping = (MappingMongoConverter) mongoTemplate.getConverter();
        mongoMapping.afterPropertiesSet();
        return mongoTemplate;
    }

    @Bean
    public MongoTemplate configMongoTemplate() {
        MongoProperties mongoProperties = configMongoProperties();
        MongoDatabaseFactory mongoDatabaseFactory = configMongoFactory(mongoProperties);
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDatabaseFactory);
        MappingMongoConverter mongoMapping = (MappingMongoConverter) mongoTemplate.getConverter();
        mongoMapping.afterPropertiesSet();
        return mongoTemplate;
    }

    @Bean
    @Primary
    public GridFsTemplate getGridFsTemplate() {
        MongoProperties mongoProperties = gameMongoProperties();
        MongoDatabaseFactory mongoDatabaseFactory = gameMongoFactory(mongoProperties);
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDatabaseFactory);
        MappingMongoConverter mongoMapping = (MappingMongoConverter) mongoTemplate.getConverter();
        return new GridFsTemplate(mongoDatabaseFactory, mongoMapping);
    }

    @Bean
    @Primary
    public MongoDatabaseFactory gameMongoFactory(MongoProperties mongoProperties) {
        return new SimpleMongoClientDatabaseFactory(mongoProperties.getUri());
    }

    @Bean
    public MongoDatabaseFactory configMongoFactory(MongoProperties mongoProperties) {
        return new SimpleMongoClientDatabaseFactory(mongoProperties.getUri());
    }

}