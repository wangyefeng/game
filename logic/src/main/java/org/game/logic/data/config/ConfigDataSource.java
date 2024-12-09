package org.game.logic.data.config;

import org.springframework.boot.autoconfigure.mongo.MongoConnectionDetails;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.PropertiesMongoConnectionDetails;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoDatabaseFactorySupport;
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
    PropertiesMongoConnectionDetails configMongoConnectionDetails(MongoProperties properties) {
        return new PropertiesMongoConnectionDetails(properties);
    }

    @Bean
    public MongoTemplate configMongoTemplate() {
        PropertiesMongoConnectionDetails propertiesMongoConnectionDetails = configMongoConnectionDetails(configMongoProperties());
        return new MongoTemplate(configMongoDatabaseFactory(propertiesMongoConnectionDetails));
    }

    @Bean
    MongoDatabaseFactorySupport<?> configMongoDatabaseFactory(MongoConnectionDetails connectionDetails) {
        return new SimpleMongoClientDatabaseFactory(connectionDetails.getConnectionString().getConnectionString());
    }

}