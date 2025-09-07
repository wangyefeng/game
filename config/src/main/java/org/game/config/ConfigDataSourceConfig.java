package org.game.config;

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
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 配置 config 数据源（JPA）
 */
@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = "org.game.config.entity")
@EnableJpaRepositories(
        basePackages = "org.game.config.repository",
        entityManagerFactoryRef = "configEntityManagerFactoryBean",
        transactionManagerRef = "configTransactionManager"
)
public class ConfigDataSourceConfig {

    private final JpaProperties jpaProperties;
    private final HibernateProperties hibernateProperties;

    public ConfigDataSourceConfig(JpaProperties jpaProperties, HibernateProperties hibernateProperties) {
        this.jpaProperties = jpaProperties;
        this.hibernateProperties = hibernateProperties;
    }

    @Bean(name = "dataSourceConfig")
    @ConfigurationProperties(prefix = "spring.datasource.config")
    public DataSource configDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "configEntityManagerFactoryBean")
    public LocalContainerEntityManagerFactoryBean configEntityManagerFactoryBean(
            EntityManagerFactoryBuilder builder,
            @Qualifier("dataSourceConfig") DataSource dataSource) {

        Map<String, Object> properties = hibernateProperties.determineHibernateProperties(
                jpaProperties.getProperties(), new HibernateSettings());

        return builder
                .dataSource(dataSource)
                .properties(properties)
                .packages("org.game.config.entity")
                .persistenceUnit("configPersistenceUnit")
                .build();
    }

    @Bean(name = "configTransactionManager")
    public JpaTransactionManager configTransactionManager(
            @Qualifier("configEntityManagerFactoryBean") LocalContainerEntityManagerFactoryBean factoryBean) {
        assert factoryBean.getObject() != null;
        return new JpaTransactionManager(factoryBean.getObject());
    }
}
