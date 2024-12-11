package org.game.logic.data;

import org.springframework.boot.autoconfigure.mongo.MongoConnectionDetails;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.PropertiesMongoConnectionDetails;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoDatabaseFactorySupport;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "org.game.logic.data.repository", mongoTemplateRef = "gameMongoTemplate")
public class GameDataSource {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.data.mongodb.game")
    public MongoProperties gameMongoProperties() {
        return new MongoProperties();
    }

    @Bean
    @Primary
    PropertiesMongoConnectionDetails gameMongoConnectionDetails(MongoProperties properties) {
        return new PropertiesMongoConnectionDetails(properties);
    }

    @Bean
    @Primary
    public MongoTemplate gameMongoTemplate() {
        PropertiesMongoConnectionDetails propertiesMongoConnectionDetails = gameMongoConnectionDetails(gameMongoProperties());
        return new MongoTemplate(gameMongoDatabaseFactory(propertiesMongoConnectionDetails));
    }

    @Bean
    @Primary
    MongoDatabaseFactorySupport<?> gameMongoDatabaseFactory(MongoConnectionDetails connectionDetails) {
        return new SimpleMongoClientDatabaseFactory(connectionDetails.getConnectionString().getConnectionString());
    }
}