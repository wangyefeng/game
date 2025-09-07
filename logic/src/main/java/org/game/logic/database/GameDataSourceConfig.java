package org.game.logic.database;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = "org.game.logic.database.entity")
@EnableJpaRepositories(
        basePackages = "org.game.logic.database.repository",
        entityManagerFactoryRef = "gameEntityManagerFactoryBean",
        transactionManagerRef = "gameTransactionManager",
        repositoryBaseClass = BaseRepository.class
)
public class GameDataSourceConfig {

    private final JpaProperties jpaProperties;
    private final HibernateProperties hibernateProperties;

    public GameDataSourceConfig(JpaProperties jpaProperties, HibernateProperties hibernateProperties) {
        this.jpaProperties = jpaProperties;
        this.hibernateProperties = hibernateProperties;
    }

    @Primary
    @Bean(name = "dataSourceGame")
    @ConfigurationProperties(prefix = "spring.datasource.game")
    public DataSource dataSourceGame() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "gameEntityManagerFactoryBean")
    public LocalContainerEntityManagerFactoryBean gameEntityManagerFactoryBean(
            EntityManagerFactoryBuilder builder,
            @Qualifier("dataSourceGame") DataSource dataSource) {

        Map<String, Object> properties = hibernateProperties.determineHibernateProperties(
                jpaProperties.getProperties(), new HibernateSettings());

        return builder
                .dataSource(dataSource)
                .properties(properties)
                .packages("org.game.logic.database.entity")
                .persistenceUnit("gamePersistenceUnit")
                .build();
    }

    @Primary
    @Bean(name = "gameTransactionManager")
    public JpaTransactionManager gameTransactionManager(
            @Qualifier("gameEntityManagerFactoryBean") LocalContainerEntityManagerFactoryBean factoryBean) {
        assert factoryBean.getObject() != null;
        return new JpaTransactionManager(factoryBean.getObject());
    }
}
