package com.sakila.sakila_project.infrastructure.config.database;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.sakila.sakila_project.domain.ports.output.repositories.sakila",
        entityManagerFactoryRef = "SakilaEMF",
        transactionManagerRef = "SakilaTM"
)
@EnableTransactionManagement
public class SakilaJpaConfiguration {

    @Primary
    @Bean("SakilaEMF")
    public LocalContainerEntityManagerFactoryBean SKEntityManagerFactory(
            @Qualifier("SakilaDS") DataSource dataSource,
            EntityManagerFactoryBuilder builder
    ){
        return builder
                .dataSource(dataSource)
                .persistenceUnit("sakilaDb")
                .packages("com.sakila.sakila_project.domain.model.sakila")
                .build();
    }

    @Primary
    @Bean("SakilaTM")
    public JpaTransactionManager SKTransactionManager(
            @Qualifier("SakilaEMF") LocalContainerEntityManagerFactoryBean entityManagerFactory
    ) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject(), "sakila transaction manager has required"));
    }

}
