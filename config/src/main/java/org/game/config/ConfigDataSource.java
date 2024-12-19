package org.game.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoConnectionDetails;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.PropertiesMongoConnectionDetails;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.core.MongoDatabaseFactorySupport;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "org.game.config.data.repository", mongoTemplateRef = "configMongoTemplate")
@EnableJpaRepositories(basePackages = "org.game.config.data.repository")
@EntityScan(basePackages = "org.game.config.data.entity")
public class ConfigDataSource {

    @Bean
    @ConfigurationProperties(prefix = "spring.data.mongodb.config")
    public MongoProperties configMongoProperties() {
        return new MongoProperties();
    }

    @Bean
    public MongoTemplate configMongoTemplate() {
        PropertiesMongoConnectionDetails propertiesMongoConnectionDetails = new PropertiesMongoConnectionDetails(configMongoProperties());
        return new MongoTemplate(configMongoDatabaseFactory(propertiesMongoConnectionDetails));
    }

    @Bean
    MongoDatabaseFactorySupport<?> configMongoDatabaseFactory(MongoConnectionDetails connectionDetails) {
        return new SimpleMongoClientDatabaseFactory(connectionDetails.getConnectionString().getConnectionString());
    }

}