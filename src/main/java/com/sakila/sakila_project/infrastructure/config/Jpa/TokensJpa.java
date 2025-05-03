package com.sakila.sakila_project.infrastructure.config.Jpa;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.sakila.sakila_project.domain.ports.output.repositories.tokens",
        entityManagerFactoryRef = "TokensEMF",
        transactionManagerRef = "TokensTM"
)
@EnableTransactionManagement
public class TokensJpa {
    @Value("${spring.datasource.tokens.dialect}")
    private String dialect;

    @Bean("TokensEMF")
    public LocalContainerEntityManagerFactoryBean TKManagerFactory(
            @Qualifier("TokensDS") DataSource dataSource,
            EntityManagerFactoryBuilder builder
            ) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", dialect);

        return builder
                .dataSource(dataSource)
                .persistenceUnit("tokensDb")
                .packages("com.sakila.sakila_project.domain.model.tokens")
                .properties(properties)
                .build();
    }

    @Bean("TokensTM")
    public JpaTransactionManager TKManager(
            @Qualifier("TokensEMF") LocalContainerEntityManagerFactoryBean emf
    ) {
        return new JpaTransactionManager(Objects.requireNonNull(emf.getObject(), "Tokens Manager factory are required"));
    }

}
