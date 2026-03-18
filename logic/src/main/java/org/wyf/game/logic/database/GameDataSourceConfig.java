package org.wyf.game.logic.database;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.hibernate.autoconfigure.HibernateProperties;
import org.springframework.boot.hibernate.autoconfigure.HibernateSettings;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
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
@EntityScan(basePackages = "org.wyf.game.logic.database.entity")
@EnableJpaRepositories(
        basePackages = "org.wyf.game.logic.database.repository",
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
    public DataSource dataSourceGame(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
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
                .packages("org.wyf.game.logic.database.entity")
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
